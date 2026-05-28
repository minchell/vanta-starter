package com.vanta.starter.core.util;

import cn.hutool.core.map.MapUtil;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Map 数据结构辅助工具类。
 * <p>
 * 该工具类提供 Map 到 {@link Properties} 的转换、嵌套 Map 合并、带默认值读取等常见能力。
 * 方法只操作调用方传入的内存对象，不会访问数据库、缓存或网络；需要注意 {@link #mergeMap(Map, Map)} 会修改目标 Map。
 * </p>
 */
public class MapUtils {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private MapUtils() {
    }

    /**
     * 转换为 Properties 对象
     *
     * @param source 数据源
     * @return Properties 对象
     */
    public static Properties toProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    /**
     * 深度合并两个 Map。
     * <p>
     * 合并规则是：当两个 Map 存在相同 key 且值仍是 Map 时递归合并，否则使用 {@code from} 中的值覆盖 {@code to} 中的值。
     * 该方法会修改并返回 {@code to}，如果调用方需要保留原始数据，应先复制目标 Map 再调用。
     * </p>
     *
     * @param to   需要合并到的目标 Map。
     * @param from 提供覆盖或补充数据的来源 Map。
     * @return 合并后的目标 Map；可能是 {@code to}、{@code from} 或新的空 Map。
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> mergeMap(Map<String, Object> to, Map<String, Object> from) {
        if (MapUtil.isEmpty(to)) {
            return from;
        }
        if (MapUtil.isEmpty(from)) {
            return to;
        }
        if (MapUtil.isEmpty(to) && MapUtil.isEmpty(from)) {
            return new HashMap<>();
        }

        for (Map.Entry<String, Object> kv : to.entrySet()) {
            String toKey = kv.getKey();
            Object toValue = kv.getValue();
            Object fromValue = from.get(toKey);
            if (fromValue != null) {
                if (toValue instanceof Map) {
                    Map<String, Object> childTo = (Map<String, Object>) toValue;
                    mergeMap(childTo, (Map<String, Object>) fromValue);
                } else {
                    to.put(toKey, fromValue);
                }
            }
        }

        Set<String> keys = from.keySet();
        for (String key : keys) {
            if (!to.containsKey(key)) {
                to.put(key, from.get(key));
            }
        }
        return to;
    }

    /**
     * 从 Map 中读取字符串值。
     *
     * @param map          数据来源 Map。
     * @param key          需要读取的 key。
     * @param defaultValue Map 为空或 key 不存在时返回的默认值。
     * @return key 对应的字符串值；不存在时返回默认值。
     */
    public static String getString(Map<String, Object> map, String key, String defaultValue) {

        if (CollectionUtils.isEmpty(map))
            return defaultValue;

        var value = map.getOrDefault(key, defaultValue);

        return String.valueOf(value);
    }


    /**
     * 从 Map 中读取长整型值。
     * <p>
     * 当前实现要求 key 对应的值本身就是 {@code long}/{@link Long} 兼容类型，不做字符串解析。
     * </p>
     *
     * @param map          数据来源 Map。
     * @param key          需要读取的 key。
     * @param defaultValue Map 为空或 key 不存在时返回的默认值。
     * @return key 对应的长整型值；不存在时返回默认值。
     */
    public static long getLong(Map<String, Object> map, String key, long defaultValue) {

        if (CollectionUtils.isEmpty(map))
            return defaultValue;

        var value = map.getOrDefault(key, defaultValue);

        return (long) value;
    }


}
