package com.vanta.starter.web.autoconfigure.mvc.converter.time;

import cn.hutool.core.date.DateUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;

/**
 * 字符串到 {@link LocalDateTime} 的 Spring MVC 转换器。
 * <p>
 * 该转换器使用 Hutool 先解析为日期时间，再转换为 JDK {@link LocalDateTime}。
 * </p>
 */
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    /**
     * 把字符串转换为 {@link LocalDateTime}。
     *
     * @param source 日期时间字符串。
     * @return 解析后的 LocalDateTime。
     */
    @Override
    public LocalDateTime convert(String source) {
        return DateUtil.parse(source).toLocalDateTime();
    }
}
