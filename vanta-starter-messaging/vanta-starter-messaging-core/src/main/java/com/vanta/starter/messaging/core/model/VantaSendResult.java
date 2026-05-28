package com.vanta.starter.messaging.core.model;

/**
 * 通用消息发送结果。
 *
 * <p>该结果用于屏蔽不同 MQ 对发送状态和消息 ID 的命名差异。
 * 业务仍然可以通过 {@code rawStatus} 获取底层中间件原始状态文本。</p>
 *
 * @param success   是否发送成功
 * @param provider  中间件名称，例如 rocketmq、kafka、rabbitmq
 * @param messageId 中间件返回的消息 ID，底层没有返回时允许为空
 * @param rawStatus 底层原始状态，便于排障
 * @param error     发送失败异常，成功时为空
 */
public record VantaSendResult(
        boolean success,
        String provider,
        String messageId,
        String rawStatus,
        Throwable error
) {

    /**
     * 创建成功结果对象。
     * <p>
     * 具体 MQ 发送成功后调用该方法，把中间件名称、消息 ID 和原始状态统一转换为 Vanta 结果模型。
     * </p>
     *
     * @param provider  中间件名称，例如 rocketmq、kafka、rabbitmq
     * @param messageId 中间件返回的消息 ID，底层没有返回时允许为空
     * @param rawStatus 底层原始发送状态
     * @return 成功发送结果
     */
    public static VantaSendResult success(String provider, String messageId, String rawStatus) {
        return new VantaSendResult(true, provider, messageId, rawStatus, null);
    }

    /**
     * 创建失败结果对象。
     * <p>
     * 发送模板捕获异常后调用该方法，避免调用方只能依赖抛异常判断发送状态。
     * </p>
     *
     * @param provider 中间件名称，例如 rocketmq、kafka、rabbitmq
     * @param error    发送失败异常，允许为空
     * @return 失败发送结果
     */
    public static VantaSendResult failure(String provider, Throwable error) {
        return new VantaSendResult(false, provider, null, error == null ? null : error.getMessage(), error);
    }
}
