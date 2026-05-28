package com.vanta.starter.web.autoconfigure.mvc.converter;

import com.vanta.starter.core.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link BaseEnum} 转换器工厂。
 * <p>
 * Spring MVC 会根据目标枚举类型从该工厂获取对应 {@link BaseEnumConverter}。
 * 转换器按目标枚举类型缓存，避免每次请求参数绑定都重新扫描枚举常量。
 * </p>
 */
@SuppressWarnings({"rawtypes", "unchecked", "NullableProblems"})
public class BaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {

    /**
     * 枚举类型到转换器的缓存。
     */
    private static final Map<Class, Converter> CONVERTER_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取目标枚举类型对应的转换器。
     *
     * @param targetType 目标枚举类型。
     * @param <T>        目标枚举泛型类型。
     * @return 字符串到目标枚举的转换器。
     */
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return CONVERTER_CACHE.computeIfAbsent(targetType, key -> new BaseEnumConverter<>(targetType));
    }
}
