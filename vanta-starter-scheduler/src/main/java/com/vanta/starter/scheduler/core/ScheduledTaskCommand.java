package com.vanta.starter.scheduler.core;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 调度任务命令。
 *
 * @param taskName  任务名称
 * @param source    任务来源
 * @param payload   任务参数
 * @param createdAt 创建时间
 * @param metadata  扩展元数据
 */
public record ScheduledTaskCommand(
        String taskName,
        String source,
        Map<String, Object> payload,
        Instant createdAt,
        Map<String, String> metadata
) {

    public ScheduledTaskCommand {
        Objects.requireNonNull(taskName, "taskName must not be null");
        payload = payload == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(payload));
        metadata = metadata == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
        createdAt = createdAt == null ? Instant.now() : createdAt;
    }
}
