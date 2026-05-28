# vanta-starter-messaging

## 1. 这个 starter 解决什么问题
`vanta-starter-messaging` 是一个 聚合模块，面向 消息能力 的独立接入入口。 它本身用于组合子能力，业务项目应优先选择具体子模块引入。

## 2. 接入方式
### 2.1 推荐引入的子模块
- `vanta-starter-messaging-core`
- `vanta-starter-messaging-rocketmq`
- `vanta-starter-messaging-kafka`

### 2.2 Maven 依赖示例
```xml
<dependency>
    <groupId>com.vanta</groupId>
    <artifactId>vanta-starter-messaging</artifactId>
</dependency>
```

## 3. 完整示例
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```
