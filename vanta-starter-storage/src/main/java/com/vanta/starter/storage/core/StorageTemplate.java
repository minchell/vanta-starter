package com.vanta.starter.storage.core;

import java.io.IOException;
import java.util.Optional;

/**
 * 统一存储模板。
 *
 * <p>本接口只定义对象存储最小能力，具体实现可以是 local、S3、MinIO 或其他存储系统。</p>
 */
public interface StorageTemplate {

    /**
     * 写入对象内容。
     *
     * @param request 写入请求，包含对象 key、输入流、内容类型和扩展元数据
     * @return 写入后的对象元数据
     * @throws IOException 底层存储写入失败时抛出，例如本地磁盘不可写或远程存储不可用
     */
    StorageObjectMetadata put(StoragePutRequest request) throws IOException;

    /**
     * 读取对象内容。
     *
     * @param objectKey 对象 key，具体实现必须校验路径或 key 的合法性
     * @return 对象存在时返回 StorageObject；不存在时返回 Optional.empty()
     * @throws IOException 底层存储读取失败时抛出
     */
    Optional<StorageObject> get(String objectKey) throws IOException;

    /**
     * 删除对象。
     *
     * @param objectKey 对象 key
     * @return true 表示对象存在并已删除，false 表示对象原本不存在
     * @throws IOException 底层存储删除失败时抛出
     */
    boolean delete(String objectKey) throws IOException;

    /**
     * 判断对象是否存在。
     *
     * @param objectKey 对象 key
     * @return true 表示对象存在，false 表示对象不存在
     */
    boolean exists(String objectKey);
}
