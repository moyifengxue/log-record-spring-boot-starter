package io.github.moyifengxue.log.record.annotation;

import java.lang.annotation.*;

/**
 * 操作日志记录
 *
 * @author myf
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecordAnnotation {

    /**
     * 业务编码
     * EL表达式
     *
     * @return 业务编码
     */
    String bizCode();

    /**
     * 日志描述
     * EL表达式
     *
     * @return 描述
     */
    String msg();

    /**
     * 操作结果
     * 默认:成功/失败
     * 填写:(成功描述)/失败
     * EL表达式
     *
     * @return 操作结果
     */
    String result() default "";

    /**
     * 接口入参
     * EL表达式
     *
     * @return 接口入参
     */
    String param();

    /**
     * 文件ID
     * EL表达式
     *
     * @return 文件ID
     */
    String fileId();

    /**
     * 业务ID
     * EL表达式
     *
     * @return 业务ID
     */
    String bizId();

    /**
     * 额外信息
     * EL表达式
     *
     * @return 额外信息
     */
    String extra();
}
