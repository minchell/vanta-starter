package com.vanta.starter.core.enums;

import java.io.Serializable;
import java.util.Objects;

/**
 * 具备“值、描述、颜色”语义的基础枚举契约。
 * <p>
 * 业务枚举实现该接口后，可以被通用下拉框、字典转换、接口响应和参数校验工具统一识别。
 * 泛型 {@code T} 表示枚举值类型，通常是 {@link Integer}、{@link String} 或其他可序列化的稳定值。
 * </p>
 *
 * @param <T> 枚举值类型，必须可序列化，保证接口响应、缓存和日志输出具备稳定表达。
 */
@SuppressWarnings("rawtypes")
public interface BaseEnum<T extends Serializable> {

    /**
     * 根据枚举值获取
     *
     * @param value 枚举值
     * @param clazz 枚举类
     * @return 枚举对象
     */
    static <E extends Enum<E> & BaseEnum, T> E getByValue(T value, Class<E> clazz) {
        for (E e : clazz.getEnumConstants()) {
            if (Objects.equals(e.getValue(), value)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据枚举描述获取
     *
     * @param description 枚举描述
     * @param clazz       枚举类
     * @return 枚举对象
     */
    @SuppressWarnings("unchecked")
    static <E extends Enum<E> & BaseEnum> E getByDescription(String description, Class<?> clazz) {
        for (Object e : clazz.getEnumConstants()) {
            if (e instanceof BaseEnum<?> baseEnum && Objects.equals(baseEnum.getDescription(), description)) {
                return (E) baseEnum;
            }
        }
        return null;
    }

    /**
     * 判断枚举值是否有效
     *
     * @param value 枚举值
     * @param clazz 枚举类
     * @return 是否有效
     */
    static <E extends Enum<E> & BaseEnum, T> boolean isValidValue(T value, Class<E> clazz) {
        return getByValue(value, clazz) != null;
    }

    /**
     * 枚举值
     *
     * @return 枚举值
     */
    T getValue();

    /**
     * 枚举描述
     *
     * @return 枚举描述
     */
    String getDescription();

    /**
     * 颜色
     *
     * @return 颜色
     */
    default String getColor() {
        return null;
    }
}
