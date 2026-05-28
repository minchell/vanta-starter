package com.vanta.starter.log.enums;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * 日志字段采集范围。
 *
 * <p>全局配置和 {@code @Log} 注解都通过该枚举控制请求、响应、客户端环境等字段是否进入日志记录。
 * 默认只采集请求头、响应头、请求参数和响应参数，避免默认记录过大的请求体或响应体。</p>
 */
public enum Include {

    /**
     * 描述
     */
    DESCRIPTION,

    /**
     * 模块
     */
    MODULE,

    /**
     * 请求头（默认）
     */
    REQUEST_HEADERS,

    /**
     * 请求体（如包含请求体，则请求参数无效）
     */
    REQUEST_BODY,

    /**
     * 请求参数（默认）
     */
    REQUEST_PARAM,

    /**
     * IP 归属地
     */
    IP_ADDRESS,

    /**
     * 浏览器
     */
    BROWSER,

    /**
     * 操作系统
     */
    OS,

    /**
     * 响应头（默认）
     */
    RESPONSE_HEADERS,

    /**
     * 响应体（如包含响应体，则响应参数无效）
     */
    RESPONSE_BODY,

    /**
     * 响应参数（默认）
     */
    RESPONSE_PARAM,
    ;

    /**
     * 默认采集字段集合。
     *
     * <p>保持不可变，避免运行期被业务代码意外修改。</p>
     */
    private static final Set<Include> DEFAULT_INCLUDES;

    static {
        Set<Include> defaultIncludes = new LinkedHashSet<>();
        defaultIncludes.add(Include.REQUEST_HEADERS);
        defaultIncludes.add(Include.RESPONSE_HEADERS);
        defaultIncludes.add(Include.REQUEST_PARAM);
        defaultIncludes.add(Include.RESPONSE_PARAM);
        DEFAULT_INCLUDES = Collections.unmodifiableSet(defaultIncludes);
    }

    /**
     * 获取默认采集字段集合。
     *
     * @return 默认采集字段集合
     */
    public static Set<Include> defaultIncludes() {
        return DEFAULT_INCLUDES;
    }
}
