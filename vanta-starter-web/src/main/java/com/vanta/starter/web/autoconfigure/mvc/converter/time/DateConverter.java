package com.vanta.starter.web.autoconfigure.mvc.converter.time;

import cn.hutool.core.date.DateUtil;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * 字符串到 {@link Date} 的 Spring MVC 转换器。
 * <p>
 * 该转换器使用 Hutool {@link DateUtil#parse(CharSequence)} 解析多种常见日期时间格式，
 * 用于请求参数绑定到 {@link Date} 类型。
 * </p>
 */
public class DateConverter implements Converter<String, Date> {

    /**
     * 把字符串转换为 {@link Date}。
     *
     * @param source 日期时间字符串。
     * @return 解析后的 Date 对象。
     */
    @Override
    public Date convert(String source) {
        return DateUtil.parse(source);
    }
}
