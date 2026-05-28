package com.vanta.starter.web.autoconfigure.mvc.converter;

import com.vanta.starter.core.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link BaseEnum} 字符串到枚举的转换器。
 * <p>
 * Spring MVC 在绑定请求参数时，可以使用该转换器把字符串枚举值转换为实现 {@link BaseEnum} 的枚举实例。
 * 转换规则基于 {@link BaseEnum#getValue()}，而不是枚举名称。
 * </p>
 *
 * @param <T> 实现 BaseEnum 的枚举类型。
 */
public class BaseEnumConverter<T extends BaseEnum> implements Converter<String, T> {

    /**
     * 枚举值到枚举实例的映射缓存。
     */
    private final Map<String, T> enumMap = new HashMap<>();

    /**
     * 创建指定枚举类型的转换器。
     *
     * @param enumType 枚举类型。
     */
    public BaseEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(String.valueOf(e.getValue()), e);
        }
    }

    /**
     * 把字符串枚举值转换为枚举实例。
     *
     * @param source 请求参数中的字符串值。
     * @return 对应枚举实例；没有匹配值时返回 {@code null}。
     */
    @Override
    public T convert(String source) {
        return enumMap.get(source);
    }
}
