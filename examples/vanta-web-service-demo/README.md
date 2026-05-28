# vanta-web-service-demo

## 作用

这是 `vanta-starter-web-service` 的最小 Web 服务模板，用于内部新项目快速复制或对照搭建。

## 展示能力

- `/health`：基础存活接口。
- `/demo/success`：统一响应成功结构。
- `/demo/validation`：参数校验失败格式。
- `/demo/error`：全局异常格式。
- `/demo/time`：日期时间 JSON 序列化。
- `/v3/api-docs`、`/swagger-ui.html`：接口文档。

## 运行

```bash
mvn -pl examples/vanta-web-service-demo -am spring-boot:run
```

复制到新项目后，先确保内部 Maven 仓库或本机 Maven 仓库已有 `com.vanta:vanta-starter-web-service`，再在新项目目录执行 `mvn spring-boot:run`。

## 依赖边界

模板只直接依赖 `vanta-starter-web-service`。数据库、缓存、认证、消息、锁、文件和调度等能力按需追加对应 starter。
