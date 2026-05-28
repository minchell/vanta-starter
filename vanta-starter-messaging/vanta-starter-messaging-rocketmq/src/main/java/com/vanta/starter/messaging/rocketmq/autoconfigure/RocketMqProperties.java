package com.vanta.starter.messaging.rocketmq.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Vanta RocketMQ 增强配置。
 *
 * <p>这里的配置只控制 Vanta 封装层行为，RocketMQ 官方连接参数仍然使用
 * {@code rocketmq.*} 原生配置，避免把官方配置空间复制一份。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.ROCKETMQ)
public class RocketMqProperties {

    /**
     * 是否启用 Vanta RocketMQ 增强封装。
     * <p>true 表示注册 Vanta RocketMQ 模板，false 表示不注册</p>
     * <p>默认关闭，防止业务项目只引入依赖就触发远程 MQ 连接。</p>
     */
    private boolean enabled = false;

    /**
     * 未显式传入 tag 时使用的默认 tag。
     * <p>未显式指定 tag 时使用的默认 tag</p>
     */
    private String defaultTag = "*";

    /**
     * 同步发送默认超时时间。
     */
    private Duration sendTimeout = Duration.ofSeconds(3);

    /**
     * 获取是否启用 Vanta RocketMQ 增强封装。
     *
     * @return 是否启用 Vanta RocketMQ 增强封装
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 Vanta RocketMQ 增强封装。
     *
     * @param enabled 是否启用 Vanta RocketMQ 增强封装
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取未显式传入 tag 时使用的默认 tag。
     *
     * @return 未显式传入 tag 时使用的默认 tag
     */
    public String getDefaultTag() {
        return defaultTag;
    }

    /**
     * 设置未显式传入 tag 时使用的默认 tag。
     *
     * @param defaultTag 未显式传入 tag 时使用的默认 tag
     */
    public void setDefaultTag(String defaultTag) {
        this.defaultTag = defaultTag;
    }

    /**
     * 获取同步发送默认超时时间。
     *
     * @return 同步发送默认超时时间
     */
    public Duration getSendTimeout() {
        return sendTimeout;
    }

    /**
     * 设置同步发送默认超时时间。
     *
     * @param sendTimeout 同步发送默认超时时间
     */
    public void setSendTimeout(Duration sendTimeout) {
        this.sendTimeout = sendTimeout;
    }
}
