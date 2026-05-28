package com.vanta.starter.observability.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Vanta 观测能力配置。
 *
 * <p>本 starter 首版只提供本地观测上下文，不会主动接入远程指标、日志或链路平台。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.OBSERVABILITY)
public class ObservabilityProperties {

    /**
     * 是否启用 Vanta 观测上下文能力。
     * <p>true 表示自动配置会注册默认观测上下文 Bean；false 表示 starter 不生效</p>
     */
    private boolean enabled = false;

    /**
     * 获取是否启用 Vanta 观测上下文能力。
     *
     * @return 是否启用 Vanta 观测上下文能力
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 Vanta 观测上下文能力。
     *
     * @param enabled 是否启用 Vanta 观测上下文能力
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
