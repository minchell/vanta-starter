package com.vanta.starter.messaging.kafka.core;

import com.vanta.starter.messaging.core.model.VantaMessage;
import com.vanta.starter.messaging.core.model.VantaSendOptions;
import com.vanta.starter.messaging.core.model.VantaSendResult;
import com.vanta.starter.messaging.core.spi.VantaMessageHeaderCustomizer;
import com.vanta.starter.messaging.core.spi.VantaMessageKeyResolver;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Vanta Kafka 发送模板。
 *
 * <p>该模板把 Spring Kafka 的发送结果转换为 Vanta 通用发送结果，同时统一处理
 * 消息 key、消息头和发送失败异常。底层 producer 配置仍由 {@code spring.kafka.*} 控制。</p>
 */
public class VantaKafkaTemplate {

    /**
     * 统一发送结果中的中间件名称。
     */
    private static final String PROVIDER = "kafka";

    /**
     * Spring Kafka 原生发送模板。
     * <p>底层 broker、序列化器、事务等能力仍由业务项目的 {@code spring.kafka.*} 配置决定。</p>
     */
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    /**
     * 消息 key 解析器。
     * <p>用于从通用消息信封中解析 Kafka record key，业务方可以替换该扩展点实现领域化分区策略。</p>
     */
    private final VantaMessageKeyResolver keyResolver;

    /**
     * 消息头定制扩展点集合。
     * <p>发送前依次调用，用于统一追加 traceId、tenantId、source 等跨系统排障字段。</p>
     */
    private final List<VantaMessageHeaderCustomizer> headerCustomizers;

    /**
     * 创建 Vanta Kafka 发送模板。
     *
     * @param kafkaTemplate     Spring Kafka 原生发送模板
     * @param keyResolver       消息 key 解析器
     * @param headerCustomizers 消息头定制扩展点集合
     */
    public VantaKafkaTemplate(KafkaTemplate<Object, Object> kafkaTemplate,
                              VantaMessageKeyResolver keyResolver,
                              List<VantaMessageHeaderCustomizer> headerCustomizers) {
        this.kafkaTemplate = kafkaTemplate;
        this.keyResolver = keyResolver;
        this.headerCustomizers = headerCustomizers == null ? List.of() : List.copyOf(headerCustomizers);
    }

    /**
     * 发送普通 Kafka 消息。
     *
     * @param topic   Kafka topic
     * @param payload 业务消息体
     * @return 异步发送结果
     */
    public CompletableFuture<VantaSendResult> send(String topic, Object payload) {
        return send(topic, VantaMessage.of(payload), VantaSendOptions.defaults());
    }

    /**
     * 使用 Vanta 消息信封和发送选项发送 Kafka 消息。
     *
     * @param topic   Kafka topic
     * @param message Vanta 通用消息信封
     * @param options 发送选项；partitionKey 会优先作为 Kafka record key
     * @return 异步发送结果
     */
    public CompletableFuture<VantaSendResult> send(String topic, VantaMessage<?> message, VantaSendOptions options) {
        VantaSendOptions actualOptions = options == null ? VantaSendOptions.defaults() : options;
        ProducerRecord<Object, Object> record = buildRecord(topic, message, actualOptions);
        CompletableFuture<VantaSendResult> resultFuture = new CompletableFuture<>();

        kafkaTemplate.send(record).whenComplete((SendResult<Object, Object> result, Throwable throwable) -> {
            if (throwable != null) {
                resultFuture.complete(VantaSendResult.failure(PROVIDER, throwable));
                return;
            }
            String metadata = result.getRecordMetadata().topic() + "-" + result.getRecordMetadata().partition() + "@" + result.getRecordMetadata().offset();
            resultFuture.complete(VantaSendResult.success(PROVIDER, metadata, "SEND_OK"));
        });

        return resultFuture;
    }

    /**
     * 构建 Kafka 原生 ProducerRecord。
     *
     * @param topic   Kafka topic
     * @param message Vanta 通用消息信封
     * @param options 发送选项
     * @return Kafka 原生发送记录
     */
    private ProducerRecord<Object, Object> buildRecord(String topic, VantaMessage<?> message, VantaSendOptions options) {
        String key = StringUtils.hasText(options.partitionKey()) ? options.partitionKey() : keyResolver.resolveKey(message);
        ProducerRecord<Object, Object> record = new ProducerRecord<>(topic, key, message.payload());

        Map<String, Object> headers = new LinkedHashMap<>(message.headers());
        headers.putAll(options.headers());
        if (StringUtils.hasText(message.source())) {
            headers.put("source", message.source());
        }
        if (StringUtils.hasText(options.tag())) {
            headers.put("tag", options.tag());
        }
        headerCustomizers.forEach(customizer -> customizer.customize(message, headers));
        writeHeaders(record.headers(), headers);
        return record;
    }

    /**
     * 把通用消息头写入 Kafka 原生 Headers。
     *
     * @param target  Kafka 原生消息头容器
     * @param headers 通用消息头键值对
     */
    private void writeHeaders(Headers target, Map<String, Object> headers) {
        headers.forEach((name, value) -> {
            if (StringUtils.hasText(name) && value != null) {
                target.add(name, String.valueOf(value).getBytes(StandardCharsets.UTF_8));
            }
        });
    }
}
