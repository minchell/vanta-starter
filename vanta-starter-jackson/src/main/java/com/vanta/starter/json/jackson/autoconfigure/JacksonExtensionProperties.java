package com.vanta.starter.json.jackson.autoconfigure;

import com.vanta.starter.json.jackson.enums.BigNumberSerializeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JacksonExtensionProperties 类。
 * <p>该类型属于 JSON 序列化能力，负责承载 Spring Boot 配置绑定参数，并为自动配置提供可读的开关和连接参数。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@ConfigurationProperties("spring.jackson")
public class JacksonExtensionProperties {

    /**
     * 大数值序列化模式
     */
    private BigNumberSerializeMode bigNumberSerializeMode = BigNumberSerializeMode.FLEXIBLE;

    /**
     * 获取大数值序列化模式。
     *
     * @return 大数值序列化模式
     */
    public BigNumberSerializeMode getBigNumberSerializeMode() {
        return bigNumberSerializeMode;
    }

    /**
     * 设置大数值序列化模式。
     *
     * @param bigNumberSerializeMode 大数值序列化模式
     */
    public void setBigNumberSerializeMode(BigNumberSerializeMode bigNumberSerializeMode) {
        this.bigNumberSerializeMode = bigNumberSerializeMode;
    }
}
