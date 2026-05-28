package com.vanta.starter.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 时间戳有效期校验注解。
 * <p>
 * 该注解用于校验毫秒时间戳是否在当前系统时间允许误差范围内，常用于接口防重放、签名时间戳或短时有效请求校验。
 * 待校验值为 {@code null} 时会被视为无效；是否允许为空应由业务方选择是否额外组合其他约束。
 * </p>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimestampValidator.class)
@Documented
public @interface ValidTimestamp {

    /**
     * 提示消息
     *
     * @return 提示消息
     */
    String message() default "timestamp is expired";

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

    /**
     * 允许的时间误差（秒）
     *
     * @return 当前时间前后允许偏差的秒数。
     */
    long tolerance() default 300;
}
