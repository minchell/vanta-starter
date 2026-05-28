package com.vanta.starter.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link ValidTimestamp} 时间戳校验器。
 * <p>
 * 校验器使用当前系统毫秒时间与入参时间戳比较，只要二者差值不超过注解配置的容忍秒数即为通过。
 * 它不读取远程时间源，因此系统时钟准确性由部署环境保证。
 * </p>
 */
public class TimestampValidator implements ConstraintValidator<ValidTimestamp, Long> {

    /**
     * 允许的时间误差，单位秒。
     */
    private long tolerance;

    /**
     * 初始化时间戳校验器。
     *
     * @param constraintAnnotation 时间戳校验注解实例。
     */
    @Override
    public void initialize(ValidTimestamp constraintAnnotation) {
        this.tolerance = constraintAnnotation.tolerance();
    }

    /**
     * 校验时间戳是否在允许误差范围内。
     *
     * @param value   待校验毫秒时间戳。
     * @param context 校验上下文。
     * @return {@code true} 表示时间戳未过期，{@code false} 表示为空或超出允许误差。
     */
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        long now = System.currentTimeMillis();

        return Math.abs(now - value) <= (tolerance * 1000);
    }
}
