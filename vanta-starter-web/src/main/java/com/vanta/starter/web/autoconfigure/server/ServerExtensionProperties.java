package com.vanta.starter.web.autoconfigure.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Web 服务器扩展配置属性。
 * <p>
 * 该类绑定 {@code server.extension} 前缀，用于配置 Undertow 等嵌入式 Web 服务器的增强行为。
 * 当前主要能力是禁止不安全 HTTP 方法，默认禁止 CONNECT、TRACE、TRACK。
 * </p>
 */
@ConfigurationProperties("server.extension")
public class ServerExtensionProperties {

    /**
     * 默认禁止三个不安全的 HTTP 方法（如 CONNECT、TRACE、TRACK）
     */
    private static final List<String> DEFAULT_ALLOWED_METHODS = List.of("CONNECT", "TRACE", "TRACK");

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 不允许的请求方式
     */
    private List<String> disallowedMethods = new ArrayList<>(DEFAULT_ALLOWED_METHODS);

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
     * 获取不允许的请求方式。
     *
     * @return 不允许的请求方式
     */
    public List<String> getDisallowedMethods() {
        return disallowedMethods;
    }

    /**
     * 设置不允许的请求方式。
     *
     * @param disallowedMethods 不允许的请求方式
     */
    public void setDisallowedMethods(List<String> disallowedMethods) {
        this.disallowedMethods = disallowedMethods;
    }
}
