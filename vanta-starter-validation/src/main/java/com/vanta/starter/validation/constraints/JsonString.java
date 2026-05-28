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
 * JSON 字符串格式校验注解。
 * <p>
 * 该注解用于校验字符串是否是合法 JSON 文本，空字符串或空白字符串交给其他必填约束处理。
 * 适合接口入参中以字符串形式传递 JSON 配置、扩展字段或动态参数的场景。
 * </p>
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JsonStringValidator.class)
public @interface JsonString {

    /**
     * 提示消息
     *
     * @return 提示消息
     */
    String message() default "Must be in a valid JSON format";

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
