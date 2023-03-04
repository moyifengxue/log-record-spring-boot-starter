package io.github.moyifengxue.log.record.function;

import io.github.moyifengxue.log.record.annotation.LogRecordFunction;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫描申明的自定义函数
 *
 * @author zgx
 */
@Data
@Slf4j
public class CustomFunctionRegistrar implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private static Map<String, Method> functionMap = new HashMap<>();

    /**
     * 扫描声明的自定义函数
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, Object> beanWithAnnotation = applicationContext.getBeansWithAnnotation(LogRecordFunction.class);
        beanWithAnnotation.values()
                .forEach(
                        component -> {
                            Method[] methods = component.getClass().getMethods();
                            LogRecordFunction classLogRecordFunction = component.getClass().getAnnotation(LogRecordFunction.class);
                            String prefixName = classLogRecordFunction.value();
                            if (StringUtils.hasText(prefixName)) {
                                prefixName += "_";
                            }
                            for (Method method : methods) {
                                if (method.isAnnotationPresent(LogRecordFunction.class) && isStaticMethod(method)) {
                                    LogRecordFunction logRecordFunction = method.getAnnotation(LogRecordFunction.class);
                                    String registerName = StringUtils.hasText(logRecordFunction.value()) ? logRecordFunction.value() : method.getName();
                                    functionMap.put(prefixName + registerName, method);
                                    log.info("LogRecord register custom function [{}] as name [{}]", method, prefixName + registerName);
                                }
                            }
                        }
                );
    }

    /**
     * 静态方法注册到上下文
     * @param context 上下文
     */
    public static void register(StandardEvaluationContext context) {
        functionMap.forEach(context::registerFunction);
    }

    /**
     * 判断是否为静态方法
     *
     * @param method 待判断的方法
     * @return 如果为静态方法 返回true 反之false
     */
    private static boolean isStaticMethod(Method method) {
        if (method == null) {
            return false;
        }
        int modifiers = method.getModifiers();
        return Modifier.isStatic(modifiers);
    }
}
