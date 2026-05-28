package com.vanta.starter.storage.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

/**
 * 本地文件存储实现。
 *
 * <p>该实现只写入配置的 basePath 下，并通过 normalize 校验防止路径穿越。
 * 它适合本地开发、单机部署和单元测试，不适合作为多节点共享存储。</p>
 */
public class LocalFileStorageTemplate implements StorageTemplate {

    /**
     * 本地存储根目录的绝对规范路径。
     *
     * <p>所有 objectKey 都必须解析到该目录之下，防止通过 ../ 等路径片段写入或读取应用目录外文件。</p>
     */
    private final Path basePath;

    /**
     * 创建本地文件存储模板。
     *
     * @param basePath 本地存储根目录；相对路径会转换为绝对规范路径
     */
    public LocalFileStorageTemplate(Path basePath) {
        this.basePath = basePath.toAbsolutePath().normalize();
    }

    /**
     * 写入本地文件。
     *
     * @param request 写入请求，objectKey 会被解析到 basePath 下
     * @return 写入后的本地文件元数据
     * @throws IOException 创建目录或写文件失败时抛出
     */
    @Override
    public StorageObjectMetadata put(StoragePutRequest request) throws IOException {
        Path target = resolveSafePath(request.objectKey());
        Files.createDirectories(target.getParent());
        try (InputStream inputStream = request.inputStream()) {
            Files.copy(inputStream, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        return metadata(request.objectKey(), target, request.contentType());
    }

    /**
     * 读取本地文件。
     *
     * @param objectKey 对象 key
     * @return 文件存在时返回对象内容和元数据；文件不存在时返回 Optional.empty()
     * @throws IOException 打开文件流或探测内容类型失败时抛出
     */
    @Override
    public Optional<StorageObject> get(String objectKey) throws IOException {
        Path target = resolveSafePath(objectKey);
        if (!Files.exists(target) || !Files.isRegularFile(target)) {
            return Optional.empty();
        }
        return Optional.of(new StorageObject(metadata(objectKey, target, Files.probeContentType(target)), Files.newInputStream(target)));
    }

    /**
     * 删除本地文件。
     *
     * @param objectKey 对象 key
     * @return true 表示删除成功，false 表示文件不存在
     * @throws IOException 删除失败时抛出
     */
    @Override
    public boolean delete(String objectKey) throws IOException {
        return Files.deleteIfExists(resolveSafePath(objectKey));
    }

    /**
     * 判断本地文件是否存在。
     *
     * @param objectKey 对象 key
     * @return true 表示文件存在
     */
    @Override
    public boolean exists(String objectKey) {
        return Files.exists(resolveSafePath(objectKey));
    }

    /**
     * 构造本地文件元数据。
     *
     * @param objectKey   对象 key
     * @param target      已解析的本地文件路径
     * @param contentType 内容类型，可能来自写入请求或系统探测
     * @return 对象元数据
     * @throws IOException 读取文件大小或最后修改时间失败时抛出
     */
    private StorageObjectMetadata metadata(String objectKey, Path target, String contentType) throws IOException {
        return new StorageObjectMetadata(
                objectKey,
                Files.size(target),
                contentType,
                Files.getLastModifiedTime(target).toInstant(),
                java.util.Map.of("storage", "local", "createdAt", Instant.now().toString())
        );
    }

    /**
     * 将对象 key 解析为安全的本地路径。
     *
     * @param objectKey 对象 key
     * @return basePath 下的规范路径
     * @throws IllegalArgumentException 当 objectKey 试图逃逸 basePath 时抛出
     */
    private Path resolveSafePath(String objectKey) {
        Path target = basePath.resolve(objectKey).normalize();
        if (!target.startsWith(basePath)) {
            throw new IllegalArgumentException("objectKey must stay within storage base path: " + objectKey);
        }
        return target;
    }
}
