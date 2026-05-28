package com.vanta.starter.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 枚举值合法性校验注解。
 * <p>
 * 该注解用于校验字段、参数或类型使用位置上的值是否属于指定枚举范围。
 * 支持直接配置允许值列表，也支持指定枚举类和取值方法，适合接口入参枚举值校验。
 * </p>
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValueCheckValidator.class)
public @interface EnumValueCheck {

    /**
     * 枚举类
     *
     * @return 枚举类
     */
    Class<? extends Enum> value() default Enum.class;

    /**
     * 枚举值
     *
     * @return 枚举值
     */
    String[] enumValues() default {};

    /**
     * 获取枚举值的方法名
     *
     * @return 获取枚举值的方法名
     */
    String method() default "";

    /**
     * 提示消息
     *
     * @return 提示消息
     */
    String message() default "The parameter value is invalid";

    /**
     * 分组
     *
     * @return 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     *
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};
}
