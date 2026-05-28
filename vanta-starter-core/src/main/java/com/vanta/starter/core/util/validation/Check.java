package com.vanta.starter.core.util.validation;

import com.vanta.starter.core.exception.BusinessException;

/**
 * 业务校验断言。
 *
 * <p>用于在业务前置条件不满足时抛出统一的业务异常。</p>
 */
public final class Check {
    private Check() {
        throw new UnsupportedOperationException("Check 不允许实例化");
    }

    /**
     * 校验表达式必须为真。
     *
     * @param expression 待校验表达式
     * @param message 失败提示
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(message);
        }
    }

    /**
     * 校验对象不能为空。
     *
     * @param value 待校验对象
     * @param message 失败提示
     * @param <T> 对象类型
     * @return 原对象
     */
    public static <T> T notNull(T value, String message) {
        if (value == null) {
            throw new BusinessException(message);
        }
        return value;
    }
}
