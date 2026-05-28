package com.vanta.starter.ratelimiter.enums;


/**
 * LimitType 枚举。
 * <p>该类型属于 限流能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public enum LimitType {

    /**
     * 全局限流
     */
    DEFAULT,

    /**
     * 根据 IP 限流
     */
    IP,

    /**
     * 根据实例限流（支持集群多实例）
     */
    CLUSTER
}
