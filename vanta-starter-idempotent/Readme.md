# vanta-starter-idempotent

## 1. 这个 starter 解决什么问题
`vanta-starter-idempotent` 是一个 starter 模块，面向 幂等能力 的独立接入入口。 它具备 Spring Boot 自动装配入口，可以直接放进业务项目使用。 若要启用能力，请按下面的配置示例显式开启对应开关。

## 2. 接入方式
### 2.1 Maven 依赖
```xml
<dependency>
    <groupId>com.vanta</groupId>
    <artifactId>vanta-starter-idempotent</artifactId>
</dependency>
```

### 2.2 配置示例
```yaml
vanta-starter:
  idempotent:
    enabled: true
spring:
  data:
    redisson:
      enabled: true
```

## 3. 完整示例
```java
import com.vanta.starter.idempotent.annotation.Idempotent;
import org.springframework.stereotype.Service;

@Service
public class OrderSubmitService {

    @Idempotent(key = "#requestNo", timeout = 3000, message = "请勿重复提交")
    public String submit(String requestNo) {
        return "accepted:" + requestNo;
    }
}
```
