package com.vanta.starter.core.util;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.function.Function;

/**
 * 字符串扩展工具类。
 * <p>
 * 该类在 Hutool 字符串工具基础上补充项目常用转换方法，避免业务代码反复书写空白判断和默认值逻辑。
 * 方法只处理传入字符串，不读取配置、不访问外部资源。
 * </p>
 */
public class StrUtils {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private StrUtils() {
    }

    /**
     * 如果字符串是{@code null}或者&quot;&quot;或者空白，则返回指定默认字符串，否则针对字符串处理后返回
     *
     * @param str          要转换的字符串
     * @param defaultValue 默认值
     * @param mapper       针对字符串的转换方法
     * @param <T>          转换后的目标类型。
     * @return 转换后的字符串或指定的默认字符串
     */
    public static <T> T blankToDefault(CharSequence str, T defaultValue, Function<String, T> mapper) {
        return CharSequenceUtil.isBlank(str) ? defaultValue : mapper.apply(str.toString());
    }
}
