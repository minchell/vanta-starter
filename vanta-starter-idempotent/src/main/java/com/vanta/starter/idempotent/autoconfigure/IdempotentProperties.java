package com.vanta.starter.idempotent.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * IdempotentProperties 类。
 * <p>该类型属于 幂等能力，负责承载 Spring Boot 配置绑定参数，并为自动配置提供可读的开关和连接参数。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@ConfigurationProperties(PropertiesConstants.IDEMPOTENT)
public class IdempotentProperties {

    /**
     * Key 前缀
     */
    private String keyPrefix = "Idempotent";

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
