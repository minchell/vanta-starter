package com.vanta.starter.core.util.validation;

import cn.hutool.core.text.CharSequenceUtil;
import com.vanta.starter.core.exception.BadRequestException;

import java.util.function.BooleanSupplier;

/**
 * 请求参数校验工具类。
 * <p>
 * 该类面向请求参数、DTO 字段、调用前置条件等输入合法性校验，所有校验失败都会抛出 {@link BadRequestException}。
 * 与 {@link CheckUtils} 相比，它更适合表达调用方输入不合法，而不是业务状态冲突。
 * </p>
 */
public class ValidationUtils extends Validator {

    /**
     * 请求参数校验失败时使用的异常类型。
     */
    private static final Class<BadRequestException> EXCEPTION_TYPE = BadRequestException.class;

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private ValidationUtils() {
    }

    /**
     * 如果对象为 {@code null} 则抛出请求参数异常。
     *
     * @param obj      被检测的对象
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfNull(Object obj, String template, Object... params) {
        throwIfNull(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果对象不为 {@code null} 则抛出请求参数异常。
     *
     * @param obj      被检测的对象
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfNotNull(Object obj, String template, Object... params) {
        throwIfNotNull(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果对象为空则抛出请求参数异常。
     *
     * @param obj      被检测的对象
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfEmpty(Object obj, String template, Object... params) {
        throwIfEmpty(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果对象不为空则抛出请求参数异常。
     *
     * @param obj      被检测的对象
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfNotEmpty(Object obj, String template, Object... params) {
        throwIfNotEmpty(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果字符串为空白则抛出请求参数异常。
     *
     * @param str      被检测的字符串
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfBlank(CharSequence str, String template, Object... params) {
        throwIfBlank(str, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果字符串非空白则抛出请求参数异常。
     *
     * @param str      被检测的字符串
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfNotBlank(CharSequence str, String template, Object... params) {
        throwIfNotBlank(str, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果两个对象相同则抛出请求参数异常。
     *
     * @param obj1     要比较的对象1
     * @param obj2     要比较的对象2
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfEqual(Object obj1, Object obj2, String template, Object... params) {
        throwIfEqual(obj1, obj2, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果两个对象不相同则抛出请求参数异常。
     *
     * @param obj1     要比较的对象1
     * @param obj2     要比较的对象2
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfNotEqual(Object obj1, Object obj2, String template, Object... params) {
        throwIfNotEqual(obj1, obj2, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果两个字符串相同则抛出请求参数异常（不区分大小写）。
     *
     * @param str1     要比较的字符串1
     * @param str2     要比较的字符串2
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfEqualIgnoreCase(CharSequence str1, CharSequence str2, String template, Object... params) {
        throwIfEqualIgnoreCase(str1, str2, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果两个字符串不相同则抛出请求参数异常（不区分大小写）。
     *
     * @param str1     要比较的字符串1
     * @param str2     要比较的字符串2
     * @param template 异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params   参数值
     */
    public static void throwIfNotEqualIgnoreCase(CharSequence str1,
                                                 CharSequence str2,
                                                 String template,
                                                 Object... params) {
        throwIfNotEqualIgnoreCase(str1, str2, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果条件成立则抛出请求参数异常。
     *
     * @param condition 条件
     * @param template  异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params    参数值
     */
    public static void throwIf(boolean condition, String template, Object... params) {
        throwIf(condition, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    /**
     * 如果条件提供者返回 {@code true} 则抛出请求参数异常。
     *
     * @param conditionSupplier 条件
     * @param template          异常信息模板，被替换的部分用 {} 表示，如果模板为 null，返回 "null"
     * @param params            参数值
     */
    public static void throwIf(BooleanSupplier conditionSupplier, String template, Object... params) {
        throwIf(conditionSupplier, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }
}
