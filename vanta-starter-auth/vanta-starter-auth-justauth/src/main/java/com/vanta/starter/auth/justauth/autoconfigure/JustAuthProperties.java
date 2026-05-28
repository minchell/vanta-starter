package com.vanta.starter.auth.justauth.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import me.zhyd.oauth.config.AuthConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * JustAuth 配置属性
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 */
@ConfigurationProperties(PropertiesConstants.AUTH_JUSTAUTH)
public class JustAuthProperties {

    /**
     * 是否启用
     * -- GETTER --
     * 读取 Enabled 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     * <p>
     * <p>
     * -- SETTER --
     * 设置 Enabled 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @param enabled enabled 参数，调用方应传入与 认证授权能力 场景匹配的有效值
     */
    private boolean enabled = false;

    /**
     * 第三方平台配置
     * -- GETTER --
     * 读取 Type 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     * <p>
     * <p>
     * -- SETTER --
     * 设置 Type 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @param type type 参数，调用方应传入与 认证授权能力 场景匹配的有效值
     */
    private Map<String, AuthConfig> type;

    /**
     * 自定义配置
     * -- GETTER --
     * 读取 Extend 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     * <p>
     * <p>
     * -- SETTER --
     * 设置 Extend 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @param extend extend 参数，调用方应传入与 认证授权能力 场景匹配的有效值
     */
    @NestedConfigurationProperty
    private JustAuthExtendProperties extend;

    /**
     * 缓存配置
     * -- GETTER --
     * 读取 Cache 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     * <p>
     * <p>
     * -- SETTER --
     * 设置 Cache 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @param cache cache 参数，调用方应传入与 认证授权能力 场景匹配的有效值
     */
    @NestedConfigurationProperty
    private JustAuthCacheProperties cache;

    /**
     * HTTP 配置
     * -- GETTER --
     * 读取 Http 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     * <p>
     * <p>
     * -- SETTER --
     * 设置 Http 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @param http http 参数，调用方应传入与 认证授权能力 场景匹配的有效值
     */
    @NestedConfigurationProperty
    private JustAuthHttpProperties http;

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
     * 获取第三方平台配置。
     *
     * @return 第三方平台配置
     */
    public Map<String, AuthConfig> getType() {
        return type;
    }

    /**
     * 设置第三方平台配置。
     *
     * @param type 第三方平台配置
     */
    public void setType(Map<String, AuthConfig> type) {
        this.type = type;
    }

    /**
     * 获取自定义配置。
     *
     * @return 自定义配置
     */
    public JustAuthExtendProperties getExtend() {
        return extend;
    }

    /**
     * 设置自定义配置。
     *
     * @param extend 自定义配置
     */
    public void setExtend(JustAuthExtendProperties extend) {
        this.extend = extend;
    }

    /**
     * 获取缓存配置。
     *
     * @return 缓存配置
     */
    public JustAuthCacheProperties getCache() {
        return cache;
    }

    /**
     * 设置缓存配置。
     *
     * @param cache 缓存配置
     */
    public void setCache(JustAuthCacheProperties cache) {
        this.cache = cache;
    }

    /**
     * 获取 HTTP 配置。
     *
     * @return HTTP 配置
     */
    public JustAuthHttpProperties getHttp() {
        return http;
    }

    /**
     * 设置 HTTP 配置。
     *
     * @param http HTTP 配置
     */
    public void setHttp(JustAuthHttpProperties http) {
        this.http = http;
    }
}
