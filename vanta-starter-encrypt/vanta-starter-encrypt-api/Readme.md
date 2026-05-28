# vanta-starter-encrypt-api

## 1. 组件作用
API 扩展模块，主要面向接口层增强和 HTTP 入口适配。

主要公开类型：
- `ApiEncryptAutoConfiguration`
- `ApiEncryptProperties`
- `ApiEncryptFilter`
- `RequestBodyDecryptWrapper`
- `ResponseBodyEncryptWrapper`
- `ApiEncrypt`

## 2. 适用场景
- 需要面向 HTTP 接口做增强。
- 需要在 API 层保留统一的自动装配入口。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-encrypt-api</artifactId>
</dependency>
```

主要扩展点：
- `ApiEncryptAutoConfiguration`
- `ApiEncryptFilter`
