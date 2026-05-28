package com.vanta.starter.validation.constraints;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link JsonString} JSON 字符串校验器。
 * <p>
 * 空白值默认视为通过，是否必填应由 {@code @NotBlank} 等标准约束负责；非空值必须符合 JSON 对象或数组等 JSON 格式。
 * </p>
 */
public class JsonStringValidator implements ConstraintValidator<JsonString, String> {

    /**
     * 校验字符串是否为合法 JSON。
     *
     * @param value   待校验字符串。
     * @param context 校验上下文。
     * @return {@code true} 表示为空白或合法 JSON，{@code false} 表示非空但不是合法 JSON。
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (CharSequenceUtil.isBlank(value)) {
            return true;
        }
        return JSONUtil.isTypeJSON(value);
    }
}
