package com.vanta.starter.core.util.validation;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * 校验工具基础类。
 * <p>
 * 该类沉淀空值、相等性、布尔条件和 JSR 303 Bean Validation 的公共校验逻辑。
 * 子类通过传入不同异常类型区分请求参数错误和业务规则错误，避免重复编写相同判断代码。
 * </p>
 */
public class Validator {

    /**
     * Jakarta Bean Validation 校验器。
     * <p>
     * 从 Spring 容器获取，要求接入项目已经启用标准校验 Bean。
     * </p>
     */
    private static final jakarta.validation.Validator VALIDATOR = SpringUtil.getBean(jakarta.validation.Validator.class);

    /**
     * 受保护构造方法。
     * <p>
     * 允许工具子类继承公共校验能力，但不鼓励业务代码直接实例化。
     * </p>
     */
    protected Validator() {
    }

    /**
     * 如果对象为 {@code null} 则抛出指定运行时异常。
     *
     * @param obj           被检测的对象
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfNull(Object obj, String message, Class<? extends RuntimeException> exceptionType) {
        throwIf(obj == null, message, exceptionType);
    }

    /**
     * 如果对象不为 {@code null} 则抛出指定运行时异常。
     *
     * @param obj           被检测的对象
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfNotNull(Object obj, String message, Class<? extends RuntimeException> exceptionType) {
        throwIf(obj != null, message, exceptionType);
    }

    /**
     * 如果对象为空则抛出指定运行时异常。
     *
     * @param obj           被检测的对象
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfEmpty(Object obj, String message, Class<? extends RuntimeException> exceptionType) {
        throwIf(ObjectUtil.isEmpty(obj), message, exceptionType);
    }

    /**
     * 如果对象不为空则抛出指定运行时异常。
     *
     * @param obj           被检测的对象
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfNotEmpty(Object obj, String message, Class<? extends RuntimeException> exceptionType) {
        throwIf(ObjectUtil.isNotEmpty(obj), message, exceptionType);
    }

    /**
     * 如果字符串为空白则抛出指定运行时异常。
     *
     * @param str           被检测的字符串
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfBlank(CharSequence str,
                                       String message,
                                       Class<? extends RuntimeException> exceptionType) {
        throwIf(CharSequenceUtil.isBlank(str), message, exceptionType);
    }

    /**
     * 如果字符串非空白则抛出指定运行时异常。
     *
     * @param str           被检测的字符串
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfNotBlank(CharSequence str,
                                          String message,
                                          Class<? extends RuntimeException> exceptionType) {
        throwIf(CharSequenceUtil.isNotBlank(str), message, exceptionType);
    }

    /**
     * 如果两个对象相同则抛出指定运行时异常。
     *
     * @param obj1          要比较的对象1
     * @param obj2          要比较的对象2
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfEqual(Object obj1,
                                       Object obj2,
                                       String message,
                                       Class<? extends RuntimeException> exceptionType) {
        throwIf(ObjectUtil.equal(obj1, obj2), message, exceptionType);
    }

    /**
     * 如果两个对象不相同则抛出指定运行时异常。
     *
     * @param obj1          要比较的对象1
     * @param obj2          要比较的对象2
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfNotEqual(Object obj1,
                                          Object obj2,
                                          String message,
                                          Class<? extends RuntimeException> exceptionType) {
        throwIf(ObjectUtil.notEqual(obj1, obj2), message, exceptionType);
    }

    /**
     * 如果两个字符串相同则抛出指定运行时异常（不区分大小写）。
     *
     * @param str1          要比较的字符串1
     * @param str2          要比较的字符串2
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfEqualIgnoreCase(CharSequence str1,
                                                 CharSequence str2,
                                                 String message,
                                                 Class<? extends RuntimeException> exceptionType) {
        throwIf(CharSequenceUtil.equalsIgnoreCase(str1, str2), message, exceptionType);
    }

    /**
     * 如果两个字符串不相同则抛出指定运行时异常（不区分大小写）。
     *
     * @param str1          要比较的字符串1
     * @param str2          要比较的字符串2
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIfNotEqualIgnoreCase(CharSequence str1,
                                                    CharSequence str2,
                                                    String message,
                                                    Class<? extends RuntimeException> exceptionType) {
        throwIf(!CharSequenceUtil.equalsIgnoreCase(str1, str2), message, exceptionType);
    }

    /**
     * 如果条件成立则抛出指定运行时异常。
     *
     * @param condition     条件
     * @param message       提示信息
     * @param exceptionType 异常类型
     */
    protected static void throwIf(boolean condition, String message, Class<? extends RuntimeException> exceptionType) {
        if (condition) {
            throw ReflectUtil.newInstance(exceptionType, message);
        }
    }

    /**
     * 如果条件提供者返回 {@code true} 则抛出指定运行时异常。
     *
     * @param conditionSupplier 条件
     * @param message           错误信息
     * @param exceptionType     异常类型
     */
    protected static void throwIf(BooleanSupplier conditionSupplier,
                                  String message,
                                  Class<? extends RuntimeException> exceptionType) {
        if (conditionSupplier != null && conditionSupplier.getAsBoolean()) {
            throw ReflectUtil.newInstance(exceptionType, message);
        }
    }

    /**
     * 执行 JSR 303 Bean Validation 校验。
     * <p>
     * 当存在任意约束失败时，会抛出 {@link ConstraintViolationException}，由上层统一异常处理转换为接口错误响应。
     * </p>
     *
     * @param obj    被校验对象。
     * @param groups 校验分组。
     */
    public static void validate(Object obj, Class<?>... groups) {
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(obj, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
