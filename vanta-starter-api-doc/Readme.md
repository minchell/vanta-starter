# vanta-starter-api-doc

## 1. 组件作用
接口文档增强，整合 SpringDoc 与 NextDoc4j，并结合项目元信息填充 OpenAPI。

主要公开类型：
- `SpringDocAutoConfiguration`
- `ApiDocUtils`
- `BaseEnumProcessor`

## 2. 适用场景
- 需要自动生成 Swagger / OpenAPI 页面。
- 需要在接口文档中统一展示枚举含义和项目元信息。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-api-doc</artifactId>
</dependency>
```

默认配置示例：
```yaml
--- ### 接口文档配置
vanta-starter:
  api-doc:
    enabled: false

springdoc:
  swagger-ui:
    enabled: ${vanta-starter.api-doc.enabled:false}
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
    show-extensions: true
  api-docs:
    enabled: ${springdoc.swagger-ui.enabled}
    path: /v3/api-docs

## 接口文档增强配置
nextdoc4j:
  enabled: true
```

单独引入 `vanta-starter-api-doc` 时，默认不启用接口文档；业务项目需要显式配置 `vanta-starter.api-doc.enabled=true`。通过 `vanta-starter-web-service` 引入时，由 Web 服务底座默认开启，也可以用 `vanta-starter.web-service.api-doc.enabled=false` 关闭。

主要扩展点：
- `SpringDocAutoConfiguration`
- `ApiDocUtils`
