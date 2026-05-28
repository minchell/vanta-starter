package com.vanta.starter.storage.core;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 文件写入请求。
 *
 * @param objectKey   对象 key，相当于存储路径，不能包含路径穿越片段
 * @param inputStream 文件内容输入流，调用方负责关闭
 * @param contentType 内容类型
 * @param metadata    扩展元数据
 */
public record StoragePutRequest(
        String objectKey,
        InputStream inputStream,
        String contentType,
        Map<String, String> metadata
) {

    public StoragePutRequest {
        Objects.requireNonNull(objectKey, "objectKey must not be null");
        Objects.requireNonNull(inputStream, "inputStream must not be null");
        metadata = metadata == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
    }
}
