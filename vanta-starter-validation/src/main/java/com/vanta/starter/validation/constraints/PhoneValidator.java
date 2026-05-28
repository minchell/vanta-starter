package com.vanta.starter.validation.constraints;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link Phone} 电话号码校验器。
 * <p>
 * 校验逻辑委托 Hutool {@link PhoneUtil#isPhone(CharSequence)} 完成，只负责处理空白值放行和 Bean Validation 接口适配。
 * </p>
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    /**
     * 校验字符串是否为合法电话号码。
     *
     * @param value   待校验字符串。
     * @param context 校验上下文。
     * @return {@code true} 表示为空白或合法电话号码，{@code false} 表示格式不合法。
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (CharSequenceUtil.isBlank(value)) {
            return true;
        }
        return PhoneUtil.isPhone(value);
    }
}
