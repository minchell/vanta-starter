# vanta-starter-encrypt-field

## 1. 组件作用
字段级能力模块，主要处理实体字段的增强能力。

主要公开类型：
- `FieldEncryptAutoConfiguration`
- `FieldEncryptProperties`
- `MyBatisDecryptInterceptor`
- `MyBatisEncryptInterceptor`
- `FieldEncrypt`
- `EncryptHelper`

## 2. 适用场景
- 需要对实体字段做透明处理。
- 需要在数据入库和出库时保持一致性。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-encrypt-field</artifactId>
</dependency>
```

主要扩展点：
- `FieldEncryptAutoConfiguration`
- `MyBatisDecryptInterceptor`
- `MyBatisEncryptInterceptor`
