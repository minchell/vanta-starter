package com.vanta.starter.messaging.core.spi;

import com.vanta.starter.messaging.core.model.VantaMessage;

/**
 * 消息 key 解析扩展点。
 *
 * <p>默认实现可以直接读取 {@link VantaMessage#key()}，业务方可以替换该接口，
 * 从订单号、设备编号、用户 ID 等领域字段中生成更适合分区和幂等的 key。</p>
 */
@FunctionalInterface
public interface VantaMessageKeyResolver {

    /**
     * 解析消息 key。
     *
     * @param message 通用消息信封
     * @return 用于中间件发送的 key；返回空时由具体 starter 决定是否降级
     */
    String resolveKey(VantaMessage<?> message);
}
