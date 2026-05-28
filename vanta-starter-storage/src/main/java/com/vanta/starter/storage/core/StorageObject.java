package com.vanta.starter.storage.core;

import java.io.InputStream;

/**
 * 存储对象读取结果。
 *
 * @param metadata    对象元数据，描述对象 key、大小、内容类型和最后修改时间
 * @param inputStream 对象内容输入流，调用方读取完成后需要关闭，避免文件句柄泄漏
 */
public record StorageObject(StorageObjectMetadata metadata, InputStream inputStream) {
}
