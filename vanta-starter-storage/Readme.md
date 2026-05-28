# vanta-starter-storage

## 1. 组件作用
文件存储模板，当前提供本地文件实现。

主要公开类型：
- `StorageAutoConfiguration`
- `StorageProperties`
- `StorageTemplate`
- `LocalFileStorageTemplate`
- `StoragePutRequest`
- `StorageObject`
- `StorageObjectMetadata`

## 2. 适用场景
- 需要统一文件上传、下载、删除和元数据抽象。
- 需要把存储实现和业务使用方式分开。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-storage</artifactId>
</dependency>
```

主要扩展点：
- `StorageAutoConfiguration`
- `StorageTemplate`
- `LocalFileStorageTemplate`
