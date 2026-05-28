package com.vanta.starter.messaging.core.model;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Vanta 通用消息信封。
 *
 * <p>这个模型只描述跨 MQ 都成立的消息元信息，不要求业务消息体继承任何父类。
 * RocketMQ、Kafka、RabbitMQ 等具体 starter 可以把它转换成各自的原生消息对象。</p>
 *
 * @param key      业务消息键，通常用于幂等、排障、控制台检索和分区路由
 * @param source   消息来源，建议写模块名或业务动作，便于跨系统排查
 * @param payload  真实业务消息体，可以是任意可序列化对象
 * @param headers  需要透传到中间件消息头的扩展字段
 * @param sendTime 创建消息信封的时间，默认使用当前时间
 * @param <T>      业务消息体类型
 */
public record VantaMessage<T>(
        String key,
        String source,
        T payload,
        Map<String, Object> headers,
        Instant sendTime
) {

    public VantaMessage {
        Objects.requireNonNull(payload, "payload must not be null");
        headers = headers == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        sendTime = sendTime == null ? Instant.now() : sendTime;
    }

    /**
     * 使用最少参数创建消息信封。
     *
     * @param payload 业务消息体
     * @param <T>     业务消息体类型
     * @return 只包含业务消息体和发送时间的消息信封
     */
    public static <T> VantaMessage<T> of(T payload) {
        return new VantaMessage<>(null, null, payload, Collections.emptyMap(), Instant.now());
    }

    /**
     * 使用消息键和业务消息体创建消息信封。
     *
     * @param key     业务消息键
     * @param payload 业务消息体
     * @param <T>     业务消息体类型
     * @return 包含消息键的消息信封
     */
    public static <T> VantaMessage<T> of(String key, T payload) {
        return new VantaMessage<>(key, null, payload, Collections.emptyMap(), Instant.now());
    }
}
