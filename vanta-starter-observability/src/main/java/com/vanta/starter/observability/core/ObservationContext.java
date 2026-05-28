package com.vanta.starter.observability.core;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 单次调用或任务的观测上下文。
 *
 * <p>它只保存 traceId、spanId 和扩展字段，不绑定任何日志、指标或链路实现。
 * 远程观测平台适配可以在后续模块中读取该上下文。</p>
 *
 * @param traceId   链路 ID
 * @param spanId    当前片段 ID
 * @param source    来源模块或动作
 * @param fields    扩展观测字段
 * @param startTime 上下文创建时间
 */
public record ObservationContext(
        String traceId,
        String spanId,
        String source,
        Map<String, String> fields,
        Instant startTime
) {

    public ObservationContext {
        fields = fields == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(fields));
        startTime = startTime == null ? Instant.now() : startTime;
    }

    /**
     * 创建只有基础链路字段的观测上下文。
     *
     * <p>该工厂方法用于最小接入场景：业务方只提供 traceId、spanId 和 source，
     * starter 自动补齐空扩展字段和当前创建时间，避免调用方重复关心默认值。</p>
     *
     * @param traceId 链路 ID，用于串联一次调用链路
     * @param spanId  当前片段 ID，用于定位当前处理节点
     * @param source  来源模块或动作名称，用于区分观测上下文来自哪里
     * @return 规范化后的观测上下文
     */
    public static ObservationContext of(String traceId, String spanId, String source) {
        return new ObservationContext(traceId, spanId, source, Collections.emptyMap(), Instant.now());
    }
}
