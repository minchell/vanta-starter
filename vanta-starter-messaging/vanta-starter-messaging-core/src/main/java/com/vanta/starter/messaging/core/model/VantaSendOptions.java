package com.vanta.starter.messaging.core.model;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 通用消息发送选项。
 *
 * <p>不同 MQ 对延迟、分区、事务的支持方式不同，本对象只表达调用方意图。
 * 具体 starter 需要把这些选项转换为自身中间件支持的参数。</p>
 *
 * @param tag           消息标签，RocketMQ 可直接使用，Kafka 可映射为 header
 * @param partitionKey  分区键或路由键，Kafka 可作为 record key，其他 MQ 可按需使用
 * @param timeout       发送超时时间，为空时使用具体 starter 默认值
 * @param delayLevel    RocketMQ 延迟等级；非 RocketMQ 实现可以忽略或转为自定义延迟策略
 * @param transactional 是否按事务消息发送；不支持事务的实现必须明确抛出异常或降级
 * @param headers       额外消息头
 */
public record VantaSendOptions(
        String tag,
        String partitionKey,
        Duration timeout,
        Integer delayLevel,
        boolean transactional,
        Map<String, Object> headers
) {

    public VantaSendOptions {
        headers = headers == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(headers));
    }

    /**
     * 默认发送选项，不带延迟、事务和额外消息头。
     *
     * @return 默认发送选项
     */
    public static VantaSendOptions defaults() {
        return new VantaSendOptions(null, null, null, null, false, Collections.emptyMap());
    }
}
