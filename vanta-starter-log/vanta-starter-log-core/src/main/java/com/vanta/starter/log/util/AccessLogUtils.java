package com.vanta.starter.log.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.vanta.starter.core.util.SpringUtils;
import com.vanta.starter.log.http.RecordableHttpRequest;
import com.vanta.starter.log.model.AccessLogProperties;
import com.vanta.starter.log.model.LogProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 访问日志参数处理工具。
 * <p>
 * 该工具集中处理访问日志输出前的参数读取、敏感字段脱敏、超长参数截断和资源路径排除，
 * 使 AOP 与拦截器两种接入方式共享同一套输出策略。
 * </p>
 */
public class AccessLogUtils {

    /**
     * 静态资源路径模式
     */
    private static final List<String> RESOURCE_PATH = List.of(
            "/**/doc/**",
            "/**/doc.html",
            "/**/nextdoc/**",
            "/**/v*/api-docs/**",
            "/**/api-docs/**",
            "/**/swagger-ui/**",
            "/**/swagger-ui.html",
            "/**/swagger-resources/**",
            "/**/webjars/**",
            "/**/favicon.ico",
            "/**/static/**",
            "/**/assets/**",
            "/**/actuator/**",
            "/error",
            "/health"
    );

    private AccessLogUtils() {
    }

    /**
     * 获取参数信息
     *
     * @param request    请求对象
     * @param properties 属性
     * @return {@link String }
     */
    public static String getParam(RecordableHttpRequest request, AccessLogProperties properties) {
        // 是否需要打印请求参数
        if (!properties.isPrintRequestParam()) {
            return null;
        }

        // 参数为空返回空
        String params = request.getParams();
        if (CharSequenceUtil.isBlank(params)) {
            return null;
        }

        Object paramObj;
        if (JSONUtil.isTypeJSONArray(params)) {
            paramObj = JSONUtil.toBean(params, List.class);
        } else if (JSONUtil.isTypeJSONObject(params)) {
            paramObj = JSONUtil.toBean(params, Map.class);
        } else {
            paramObj = params;
        }

        // 是否需要对特定入参脱敏
        if (properties.isParamSensitive()) {
            paramObj = processSensitiveParams(paramObj, properties.getSensitiveParams());
        }

        // 是否自动截断超长参数值
        if (properties.isLongParamTruncate()) {
            paramObj = processTruncateLongParams(paramObj, properties.getLongParamThreshold(), properties
                    .getLongParamMaxLength(), properties.getLongParamSuffix());
        }
        return JSONUtil.toJsonStr(paramObj);
    }

    /**
     * 排除路径
     *
     * @param properties 属性
     * @param path       路径
     * @return boolean
     */
    public static boolean exclusionPath(LogProperties properties, String path) {
        // 放行路由配置的排除检查
        return properties.isMatchExcludeUri(path) || RESOURCE_PATH.stream()
                .anyMatch(resourcePath -> SpringUtils.isMatchAnt(path, resourcePath));
    }

    /**
     * 处理敏感参数，支持 Map 和 List<Map<String, Object>> 类型
     *
     * @param params          参数
     * @param sensitiveParams 敏感参数列表
     * @return 处理后的参数
     */
    @SuppressWarnings("unchecked")
    private static Object processSensitiveParams(Object params, List<String> sensitiveParams) {
        if (params instanceof Map) {
            return filterSensitiveParams((Map<String, Object>) params, sensitiveParams);
        } else if (params instanceof List) {
            return ((List<?>) params).stream()
                    .filter(Map.class::isInstance)
                    .map(item -> filterSensitiveParams((Map<String, Object>) item, sensitiveParams))
                    .collect(Collectors.toList());
        }
        return params;
    }

    /**
     * 过滤敏感参数
     *
     * @param params          参数 Map
     * @param sensitiveParams 敏感参数列表
     * @return 处理后的参数 Map
     */
    private static Map<String, Object> filterSensitiveParams(Map<String, Object> params, List<String> sensitiveParams) {
        if (params == null || params.isEmpty() || sensitiveParams == null || sensitiveParams.isEmpty()) {
            return params;
        }

        Map<String, Object> filteredParams = new HashMap<>(params);
        for (String sensitiveKey : sensitiveParams) {
            if (filteredParams.containsKey(sensitiveKey)) {
                filteredParams.put(sensitiveKey, "***");
            }
        }
        return filteredParams;
    }

    /**
     * 处理超长参数，支持 Map 和 List<Map<String, Object>> 类型
     *
     * @param params    参数
     * @param threshold 截断阈值（值长度超过该值才截断）
     * @param maxLength 最大长度
     * @param suffix    后缀（如 "..."）
     * @return 处理后的参数
     */
    @SuppressWarnings("unchecked")
    private static Object processTruncateLongParams(Object params, int threshold, int maxLength, String suffix) {
        if (params instanceof Map) {
            return truncateLongParams((Map<String, Object>) params, threshold, maxLength, suffix);
        } else if (params instanceof List) {
            return ((List<?>) params).stream()
                    .filter(Map.class::isInstance)
                    .map(item -> truncateLongParams((Map<String, Object>) item, threshold, maxLength, suffix))
                    .collect(Collectors.toList());
        }
        return params;
    }

    /**
     * 截断超长参数
     *
     * @param params    参数 Map
     * @param threshold 截断阈值（值长度超过该值才截断）
     * @param maxLength 最大长度
     * @param suffix    后缀（如 "..."）
     * @return 处理后的参数 Map
     */
    private static Map<String, Object> truncateLongParams(Map<String, Object> params,
                                                          int threshold,
                                                          int maxLength,
                                                          String suffix) {
        if (params == null || params.isEmpty()) {
            return params;
        }

        Map<String, Object> truncatedParams = new HashMap<>(params);
        for (Map.Entry<String, Object> entry : truncatedParams.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String strValue) {
                if (strValue.length() > threshold) {
                    entry.setValue(strValue.substring(0, Math.min(strValue.length(), maxLength)) + suffix);
                }
            }
        }
        return truncatedParams;
    }
}
