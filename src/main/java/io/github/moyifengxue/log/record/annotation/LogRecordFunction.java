package io.github.moyifengxue.log.record.annotation;

import java.lang.annotation.*;

/**
 * 标记EL表达式静态方法
 *
 * @author myf
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecordFunction {

    String value() default "";

}