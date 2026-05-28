# vanta-starter-log

## 1. 这个 starter 解决什么问题
`vanta-starter-log` 是一个 聚合模块，面向 日志增强能力 的独立接入入口。 它本身用于组合子能力，业务项目应优先选择具体子模块引入。

## 2. 接入方式
### 2.1 推荐引入的子模块
- `vanta-starter-log-core`
- `vanta-starter-log-interceptor`
- `vanta-starter-log-aop`

### 2.2 Maven 依赖示例
```xml
<dependency>
    <groupId>com.vanta</groupId>
    <artifactId>vanta-starter-log</artifactId>
</dependency>
```

## 3. 完整示例
```java
@RestController
@RequestMapping("/orders")
@Log(module = "订单")
public class OrderController {

    @PostMapping
    @Log(value = "创建订单", includes = {Include.REQUEST_BODY}, excludes = {Include.RESPONSE_BODY})
    public OrderResp create(@RequestBody OrderCreateReq req) {
        return orderService.create(req);
    }

    @GetMapping("/health")
    @Log(ignore = true)
    public String health() {
        return "ok";
    }
}
```
