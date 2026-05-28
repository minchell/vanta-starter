# vanta-starter-log-core

## 1. 这个 starter 解决什么问题
`vanta-starter-log-core` 是一个 starter 模块，面向 基础支撑能力 的独立接入入口。 它更偏向基础工具或核心能力，适合被其他 starter 或业务代码直接复用。 若要启用能力，请按下面的配置示例显式开启对应开关。

## 2. 接入方式
### 2.1 Maven 依赖
```xml
<dependency>
    <groupId>com.vanta</groupId>
    <artifactId>vanta-starter-log-core</artifactId>
</dependency>
```

### 2.2 配置示例
```yaml
vanta-starter:
  log:
    enabled: true
```

## 3. 完整示例
```java
import com.vanta.starter.log.annotation.Log;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoLogController {

    @Log(value = "创建订单", module = "订单")
    @PostMapping("/demo/orders")
    public String create(@RequestBody String body) {
        return "ok";
    }
}
```
