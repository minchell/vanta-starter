package com.vanta.starter.web.autoconfigure.mvc.converter.time;

import cn.hutool.core.date.DateUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;

/**
 * 字符串到 {@link LocalTime} 的 Spring MVC 转换器。
 * <p>
 * 该转换器使用 Hutool 解析日期时间字符串，然后提取时间部分。
 * </p>
 */
public class LocalTimeConverter implements Converter<String, LocalTime> {

    /**
     * 把字符串转换为 {@link LocalTime}。
     *
     * @param source 时间或日期时间字符串。
     * @return 解析后的 LocalTime。
     */
    @Override
    public LocalTime convert(String source) {
        return DateUtil.parse(source).toLocalDateTime().toLocalTime();
    }
}
