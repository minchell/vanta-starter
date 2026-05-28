# vanta-starter-messaging-rocketmq

## 1. 这个 starter 解决什么问题
`vanta-starter-messaging-rocketmq` 是一个 starter 模块，面向 可独立接入能力 的独立接入入口。 它具备 Spring Boot 自动装配入口，可以直接放进业务项目使用。 若要启用能力，请按下面的配置示例显式开启对应开关。

## 2. 接入方式
### 2.1 Maven 依赖
```xml
<dependency>
    <groupId>com.vanta</groupId>
    <artifactId>vanta-starter-messaging-rocketmq</artifactId>
</dependency>
```

### 2.2 配置示例
```yaml
vanta-starter:
  rocketmq:
    enabled: true
```

## 3. 完整示例
```java
VantaSendResult result = vantaRocketMqTemplate.send("demo-topic", "hello");
```
