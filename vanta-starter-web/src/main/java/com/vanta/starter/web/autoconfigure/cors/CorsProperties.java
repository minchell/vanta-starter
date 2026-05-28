package com.vanta.starter.web.autoconfigure.cors;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.constant.StringConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CORS 跨域配置属性。
 * <p>
 * 该类绑定 {@code vanta-starter.web.cors} 前缀，默认放开来源、方法、请求头和响应头。
 * 生产环境建议按实际前端域名收紧 {@link #allowedOrigins}，避免过宽跨域策略。
 * </p>
 */
@ConfigurationProperties(PropertiesConstants.WEB_CORS)
public class CorsProperties {

    /**
     * 通配符列表。
     */
    private static final List<String> ALL = Collections.singletonList(StringConstants.ASTERISK);

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 允许跨域的域名
     */
    private List<String> allowedOrigins = new ArrayList<>(ALL);

    /**
     * 允许跨域的请求方式
     */
    private List<String> allowedMethods = new ArrayList<>(ALL);

    /**
     * 允许跨域的请求头
     */
    private List<String> allowedHeaders = new ArrayList<>(ALL);

    /**
     * 允许跨域的响应头
     */
    private List<String> exposedHeaders = new ArrayList<>(ALL);

    /**
     * 获取是否启用。
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用。
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取允许跨域的域名。
     *
     * @return 允许跨域的域名
     */
    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * 设置允许跨域的域名。
     *
     * @param allowedOrigins 允许跨域的域名
     */
    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * 获取允许跨域的请求方式。
     *
     * @return 允许跨域的请求方式
     */
    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * 设置允许跨域的请求方式。
     *
     * @param allowedMethods 允许跨域的请求方式
     */
    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    /**
     * 获取允许跨域的请求头。
     *
     * @return 允许跨域的请求头
     */
    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    /**
     * 设置允许跨域的请求头。
     *
     * @param allowedHeaders 允许跨域的请求头
     */
    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    /**
     * 获取允许跨域的响应头。
     *
     * @return 允许跨域的响应头
     */
    public List<String> getExposedHeaders() {
        return exposedHeaders;
    }

    /**
     * 设置允许跨域的响应头。
     *
     * @param exposedHeaders 允许跨域的响应头
     */
    public void setExposedHeaders(List<String> exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
    }
}
