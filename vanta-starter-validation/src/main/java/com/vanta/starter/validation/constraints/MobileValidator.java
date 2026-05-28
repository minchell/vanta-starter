package com.vanta.starter.validation.constraints;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link Mobile} 手机号码校验器。
 * <p>
 * 校验逻辑委托 Hutool {@link PhoneUtil#isMobile(CharSequence)} 完成，只负责处理空白值放行和 Bean Validation 接口适配。
 * </p>
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {

    /**
     * 校验字符串是否为合法手机号码。
     *
     * @param value   待校验字符串。
     * @param context 校验上下文。
     * @return {@code true} 表示为空白或合法手机号码，{@code false} 表示格式不合法。
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (CharSequenceUtil.isBlank(value)) {
            return true;
        }
        return PhoneUtil.isMobile(value);
    }
}
