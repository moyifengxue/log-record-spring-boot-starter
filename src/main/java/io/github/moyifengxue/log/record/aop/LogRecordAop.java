package io.github.moyifengxue.log.record.aop;

import io.github.moyifengxue.log.record.annotation.LogRecordAnnotation;
import io.github.moyifengxue.log.record.context.LogRecordContext;
import io.github.moyifengxue.log.record.dto.LogRecordDTO;
import io.github.moyifengxue.log.record.function.CustomFunctionRegistrar;
import io.github.moyifengxue.log.record.service.IOperatorGetService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.Optional;

/**
 * 操作日志切面
 *
 * @author myf
 */
@Aspect
public class LogRecordAop {

    /**
     * 日志
     */
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * SpEL表达式
     */
    private final ExpressionParser parser = new SpelExpressionParser();
    /**
     * 表达式解析上下文，只有#{}里的内容才会被作为SqEL表达式解析
     */
    private final TemplateParserContext parserContext = new TemplateParserContext();

    /**
     * 操作用户获取服务
     */
    private final IOperatorGetService operatorGetService;

    /**
     * 构造方法
     * @param operatorGetService 扩展接口
     */
    public LogRecordAop(IOperatorGetService operatorGetService) {
        this.operatorGetService = operatorGetService;
    }

    /**
     * 切入点
     *
     * @param logRecordAnnotation 操作记录注解
     */
    @Pointcut("@annotation(logRecordAnnotation)")
    public void log(LogRecordAnnotation logRecordAnnotation) {
    }

    /**
     * 操作日志上下文
     * @param pjp pjp
     * @param logRecordAnnotation an
     * @return 原方法返回值
     * @throws Throwable 原方法异常
     */
    @Around(value = "log(logRecordAnnotation)", argNames = "pjp,logRecordAnnotation")
    public Object aroundPrintLog(ProceedingJoinPoint pjp, LogRecordAnnotation logRecordAnnotation) throws Throwable {
        Object result = null;
        LogRecordDTO logRecord = new LogRecordDTO();

        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) pjp.getSignature()).getMethod());
        Object[] args = pjp.getArgs();

        String fileId = "", bizId = "";
        int errMsgLen = 100;
        try {
            try {
                // 获取用户信息
                logRecord.setOperatorUser(Optional.ofNullable(operatorGetService.getUser()).orElse(new Object()));
                // 业务编码
                logRecord.setBizCode(this.getValueBySpEl(logRecordAnnotation.bizCode(), parameterNames, args));
                // 日志描述
                logRecord.setMsg(this.getValueBySpEl(logRecordAnnotation.msg(), parameterNames, args));
                // 操作结果
                String logResult = logRecordAnnotation.result();
                logRecord.setResult(ObjectUtils.isEmpty(logResult) ? "成功" : this.getValueBySpEl(logResult, parameterNames, args));
                // 文件ID
                fileId = this.getValueBySpEl(logRecordAnnotation.fileId(), parameterNames, args);
                // 业务ID
                bizId = this.getValueBySpEl(logRecordAnnotation.bizId(), parameterNames, args);
                // 额外信息
                logRecord.setExtra(this.getValueBySpEl(logRecordAnnotation.extra(), parameterNames, args));
            } catch (Throwable throwableAfterFuncFailure) {
                log.error("LogRecordAop doAround before function failure, error:", throwableAfterFuncFailure);
            }
            // 原方法执行
            result = pjp.proceed();
        }
        // 原方法执行异常
        catch (Throwable throwable) {
            // 方法异常执行后日志切面
            try {
                String message = throwable.getMessage();
                logRecord.setResult("失败:" + (message.length() > errMsgLen ? message.substring(0, errMsgLen) : message));
            } catch (Throwable throwableAfterFuncFailure) {
                log.error("LogRecordAop doAround function failure, error:", throwableAfterFuncFailure);
            }
            // 抛出原方法异常
            throw throwable;
        } finally {
            try {
                String responseError = operatorGetService.customResponseError(result);
                if (!ObjectUtils.isEmpty(responseError)) {
                    logRecord.setResult("失败:" + (responseError.length() > errMsgLen ? responseError.substring(0, errMsgLen) : responseError));
                }

                // 上下文配置优先
                fileId = String.valueOf(Optional.ofNullable(LogRecordContext.getVariable(LogRecordContext.CONTEXT_KEY_NAME_FILE_ID)).orElse(fileId));
                logRecord.setFileId(fileId);

                // 上下文配置优先
                bizId = String.valueOf(Optional.ofNullable(LogRecordContext.getVariable(LogRecordContext.CONTEXT_KEY_NAME_BIZ_ID)).orElse(bizId));
                logRecord.setBizId(bizId);

                // 默认信息
                logRecord.setParam(Optional.ofNullable(logRecordAnnotation.param()).orElse("") + "request:" + operatorGetService.paramFilter(args));
                logRecord.setCreateTime(new Date());

                // 清除Context：每次方法执行一次
                LogRecordContext.clearContext();
            } catch (Throwable throwableFinal) {
                log.error("LogRecordAop doAround final error", throwableFinal);
            }
        }
        return result;
    }

    /**
     * 解析SpEL表达式
     *
     * @param key            表达式
     * @param parameterNames 参数列表
     * @param values         值
     * @return keys
     */
    private String getValueBySpEl(String key, String[] parameterNames, Object[] values) {
        String spElPrefix = "#";
        if (ObjectUtils.isEmpty(key) || !key.contains(spElPrefix)) {
            return key;
        }

        //上下文
        StandardEvaluationContext context = LogRecordContext.getContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], values[i]);
        }
        // 上下文注册 自定义Function
        CustomFunctionRegistrar.register(context);

        Expression expression = parser.parseExpression(key, parserContext);
        Object value = expression.getValue(context);

        return String.valueOf(value);
    }

}
