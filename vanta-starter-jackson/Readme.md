# vanta-starter-jackson

## 1. 组件作用
统一 JSON 序列化与反序列化行为，重点处理日期、时间、枚举和长整数。

主要公开类型：
- `JacksonAutoConfiguration`
- `JacksonExtensionProperties`
- `JSONUtils`
- `BaseEnumSerializer`
- `BigNumberSerializer`
- `JSONBuilder`
- `JSONException`
- `BaseEnumDeserializer`

配置前缀：
- `spring.jackson`

## 2. 适用场景
- 需要控制接口中的日期格式、时区和时间戳表现。
- 需要让 BaseEnum 按统一规则序列化为可读值。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-jackson</artifactId>
</dependency>
```

默认配置示例：
```yaml
--- ### Spring 配置
spring:
  ## MVC 配置
  mvc:
    format:
      # 日期格式化（针对 java.util.Date）
      date-time: yyyy-MM-dd HH:mm:ss
  ## Jackson 配置
  jackson:
    # 时区配置
    time-zone: GMT+8
    # 日期格式化（针对 java.util.Date）
    date-format: yyyy-MM-dd HH:mm:ss
    # 序列化配置（Bean -> JSON）
    serialization:
      # 不允许序列化日期时以 timestamps 输出（默认：true）
      write-dates-as-timestamps: false
      # 允许序列化无属性的 Bean
      fail-on-empty-beans: false
    # 反序列化配置（JSON -> Bean）
    deserialization:
      # 允许反序列化不存在的属性
      fail-on-unknown-properties: false
```

主要扩展点：
- `JacksonAutoConfiguration`
- `JSONUtils`
- `BaseEnumSerializer`
- `BigNumberSerializer`
