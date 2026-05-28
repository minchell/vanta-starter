package com.vanta.starter.storage.core;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 存储对象元数据。
 *
 * @param objectKey    对象 key
 * @param size         字节大小
 * @param contentType  内容类型
 * @param lastModified 最后修改时间
 * @param metadata     扩展元数据
 */
public record StorageObjectMetadata(
        String objectKey,
        long size,
        String contentType,
        Instant lastModified,
        Map<String, String> metadata
) {

    public StorageObjectMetadata {
        metadata = metadata == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
    }
}
