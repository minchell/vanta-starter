# vanta-starter-messaging-core

## 1. 这个 starter 解决什么问题
`vanta-starter-messaging-core` 是一个 starter 模块，面向 基础支撑能力 的独立接入入口。 它更偏向基础工具或核心能力，适合被其他 starter 或业务代码直接复用。

## 2. 接入方式
### 2.1 Maven 依赖
```xml
<dependency>
    <groupId>com.vanta</groupId>
    <artifactId>vanta-starter-messaging-core</artifactId>
</dependency>
```

## 3. 完整示例
```java
VantaMessage<String> message = VantaMessage.of("order-10001", "payload");
VantaSendOptions options = VantaSendOptions.defaults();
```
