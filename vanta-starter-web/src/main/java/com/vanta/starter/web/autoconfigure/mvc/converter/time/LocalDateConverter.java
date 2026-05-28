package com.vanta.starter.web.autoconfigure.mvc.converter.time;

import cn.hutool.core.date.DateUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;

/**
 * 字符串到 {@link LocalDate} 的 Spring MVC 转换器。
 * <p>
 * 该转换器使用 Hutool 解析日期时间字符串，然后提取日期部分。
 * </p>
 */
public class LocalDateConverter implements Converter<String, LocalDate> {

    /**
     * 把字符串转换为 {@link LocalDate}。
     *
     * @param source 日期或日期时间字符串。
     * @return 解析后的 LocalDate。
     */
    @Override
    public LocalDate convert(String source) {
        return DateUtil.parse(source).toLocalDateTime().toLocalDate();
    }
}
