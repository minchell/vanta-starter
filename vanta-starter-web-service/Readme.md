# vanta-starter-web-service

## 组件作用

`vanta-starter-web-service` 是内部新项目的最小 Web 服务架构底座。它只聚合基础 Web 服务能力，并提供少量默认配置，让新服务引入一个依赖即可获得统一响应、全局异常、参数校验、JSON 规范、接口文档和保守 CORS 默认值。

它不包含数据库、Redis、认证、消息、锁、文件、调度等可选能力，这些能力应按需引入对应 starter。

仓库级使用标准见 [Vanta Starter 内部搬运标准](../docs/internal-starter-standard.md)。

## 聚合能力

- `vanta-starter-core`
- `vanta-starter-jackson`
- `vanta-starter-validation`
- `vanta-starter-web`
- `vanta-starter-api-doc`

## Maven 引入

```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-web-service</artifactId>
</dependency>
```

## 默认策略

```yaml
vanta-starter:
  web-service:
    enabled: true
    response:
      enabled: true
    exception:
      enabled: true
    validation:
      enabled: true
    api-doc:
      enabled: true
    cors:
      enabled: true
```

这些开关会继续映射到底层 starter：`response.enabled` 控制 `vanta-starter.web.response.enabled`，`cors.enabled` 控制 `vanta-starter.web.cors.enabled`，`api-doc.enabled` 控制 `vanta-starter.api-doc.enabled`、`springdoc.swagger-ui.enabled` 和 `springdoc.api-docs.enabled`。

## 边界

- 只负责 Web 服务基础架构组合和默认值。
- 不承载业务逻辑。
- 不连接数据库、缓存、消息或外部中间件。
- 业务项目可以通过覆盖具体 starter 的配置或 Bean 替换默认行为。
- 如果项目需要数据库、认证、缓存、日志、限流、消息、文件、调度等能力，应额外引入具体 starter，并在业务配置中显式开启。
