package com.vanta.starter.messaging.core.spi;

import com.vanta.starter.messaging.core.model.VantaMessage;

import java.util.Map;

/**
 * 消息头定制扩展点。
 *
 * <p>starter 会先放入消息信封中的 headers，再调用该扩展点。
 * 业务可以在这里统一追加 traceId、tenantId、source、operator 等跨系统排障字段。</p>
 */
@FunctionalInterface
public interface VantaMessageHeaderCustomizer {

    /**
     * 修改待发送消息头。
     *
     * @param message 通用消息信封
     * @param headers 可变消息头 Map，具体 starter 会把它转换为底层中间件消息头
     */
    void customize(VantaMessage<?> message, Map<String, Object> headers);
}
