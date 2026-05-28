package com.vanta.starter.ratelimiter.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RateLimiterProperties 类。
 * <p>该类型属于 限流能力，负责承载 Spring Boot 配置绑定参数，并为自动配置提供可读的开关和连接参数。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@ConfigurationProperties(PropertiesConstants.RATE_LIMITER)
public class RateLimiterProperties {

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
     * @param enabled enabled 参数，调用方应传入与 限流能力 场景匹配的有效值
     */
    private boolean enabled = false;

    /**
     * Key 前缀
     * -- GETTER --
     * 读取 Key Prefix 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     * <p>
     * <p>
     * -- SETTER --
     * 设置 Key Prefix 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @param keyPrefix keyPrefix 参数，调用方应传入与 限流能力 场景匹配的有效值
     */
    private String keyPrefix = "RateLimiter";

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
     * 获取 Key 前缀。
     *
     * @return Key 前缀
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * 设置 Key 前缀。
     *
     * @param keyPrefix Key 前缀
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
