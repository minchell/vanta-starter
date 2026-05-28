# vanta-starter-encrypt-core

## 1. 组件作用
核心支撑模块，提供基础抽象、公共模型和可复用工具。

主要公开类型：
- `EncryptUtils`
- `Algorithm`
- `IEncryptor`
- `AesEncryptor`
- `DesEncryptor`
- `RsaEncryptor`
- `CryptoContext`
- `Base64Encryptor`

## 2. 适用场景
- 需要基础抽象、公共模型或通用工具。
- 需要保持能力与 Web / 数据 / 消息层解耦。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-encrypt-core</artifactId>
</dependency>
```

主要扩展点：
- `EncryptUtils`
