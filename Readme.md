# vanta-starter

`vanta-starter` 是独立发布的 Spring Boot starter 仓库，沉淀可在任意业务项目中按需引入的基础组件、后端通用组件和 starter 自动装配能力。

## Maven 坐标

对外坐标保持 `com.vanta:*` 体系不变：

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.vanta</groupId>
      <artifactId>vanta-starter-dependencies</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

业务项目按需引入具体 starter 模块，例如：

```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-core</artifactId>
</dependency>
```

## 仓库结构

- 根 `pom.xml`：独立仓库聚合入口，用于本仓库构建和 IDE 导入。
- `vanta-starter-dependencies`：对外依赖管理 POM，承载统一版本、仓库、插件和通用依赖管理。
- `vanta-starter-bom`：starter 组件 BOM，统一声明第三方依赖与 `com.vanta` starter 模块版本。
- 其他 `vanta-starter-*`：可按需引入的具体能力模块。

## 模块地图
| 模块 | 作用 | 使用场景 | 文档 |
| --- | --- | --- | --- |
| `vanta-starter-dependencies` | 依赖治理模块，统一承载 starter 版本清单与父 POM 约定。 | 需要先看依赖版本和父 POM，再决定业务模块如何接入。 | [vanta-starter-dependencies](vanta-starter-dependencies/Readme.md) |
| `vanta-starter-bom` | starter 组件 BOM，统一声明第三方依赖和 `com.vanta` starter 模块版本。 | 需要查看或发布组件版本清单。 | [vanta-starter-bom](vanta-starter-bom/Readme.md) |
| `vanta-starter-core` | 基础能力底座，提供应用元信息、线程池扩展、异常体系、通用工具类和表达式工具。 | 需要统一应用名称、版本、联系人、生产环境标记等基础元信息。 | [vanta-starter-core](vanta-starter-core/Readme.md) |
| `vanta-starter-jackson` | 统一 JSON 序列化与反序列化行为，重点处理日期、时间、枚举和长整数。 | 需要控制接口中的日期格式、时区和时间戳表现。 | [vanta-starter-jackson](vanta-starter-jackson/Readme.md) |
| `vanta-starter-api-doc` | 接口文档增强，整合 SpringDoc 与 NextDoc4j，并结合项目元信息填充 OpenAPI。 | 需要自动生成 Swagger / OpenAPI 页面。 | [vanta-starter-api-doc](vanta-starter-api-doc/Readme.md) |
| `vanta-starter-validation` | 补充常用 Bean Validation 注解与校验器，覆盖手机号、座机、JSON 字符串、时间戳和枚举校验。 | 需要在请求参数和 DTO 层做可复用的业务校验。 | [vanta-starter-validation](vanta-starter-validation/Readme.md) |
| `vanta-starter-web-service` | 最小 Web 服务架构底座，组合 core、Jackson、Validation、Web 和 API Doc，并提供保守默认配置。 | 新项目需要快速启动一个规范 Web API 服务。 | [vanta-starter-web-service](vanta-starter-web-service/Readme.md) |
| `vanta-starter-web` | Web 基础能力，统一 CORS、全局响应、请求响应重复读、MVC 转换器、上传工具和 Undertow 扩展。 | 需要统一 Web API 返回结构和异常响应。 | [vanta-starter-web](vanta-starter-web/Readme.md) |
| `vanta-starter-trace` | 链路追踪能力，基于 TLog 传递 traceId 并在 Web 链路中补充上下文信息。 | 需要把 traceId 注入到请求和响应链路中。 | [vanta-starter-trace](vanta-starter-trace/Readme.md) |
| `vanta-starter-data` | 数据访问治理聚合，提供分页抽象、查询条件标记、Repository 边界、仓储级数据源路由、事务防切库和 MyBatis-Plus 基础设施适配。 | 需要防止业务层直接使用 Mapper、Wrapper、IService、MyBatis-Plus Page 或数据源切换 API。 | [vanta-starter-data](vanta-starter-data/Readme.md) |
| `vanta-starter-auth` | 认证授权能力聚合，包含 Sa-Token 扩展与 JustAuth 第三方登录。 | 需要统一登录态、第三方登录和认证扩展。 | [vanta-starter-auth](vanta-starter-auth/Readme.md) |
| `vanta-starter-cache` | 缓存能力聚合，覆盖 Spring Cache、Redis 和 JetCache。 | 需要统一本地缓存、Redis 缓存和 JetCache 接入方式。 | [vanta-starter-cache](vanta-starter-cache/Readme.md) |
| `vanta-starter-encrypt` | 加密能力聚合，覆盖接口、字段、密码和核心算法。 | 需要对接口、字段和密码做统一加密处理。 | [vanta-starter-encrypt](vanta-starter-encrypt/Readme.md) |
| `vanta-starter-excel` | Excel 能力聚合，支持 FastExcel 和 POI 两种实现。 | 需要按场景在 FastExcel 与 POI 之间切换。 | [vanta-starter-excel](vanta-starter-excel/Readme.md) |
| `vanta-starter-log` | 日志能力聚合，拆分为核心模型、拦截器和 AOP。 | 需要统一操作日志和访问日志的采集方式。 | [vanta-starter-log](vanta-starter-log/Readme.md) |
| `vanta-starter-security` | 安全防护能力聚合，覆盖脱敏、敏感词和 XSS。 | 需要对输出数据做脱敏和敏感词控制。 | [vanta-starter-security](vanta-starter-security/Readme.md) |
| `vanta-starter-idempotent` | 幂等控制能力，防止重复提交、重复消费或重复执行。 | 需要为关键接口或消息消费增加幂等检查。 | [vanta-starter-idempotent](vanta-starter-idempotent/Readme.md) |
| `vanta-starter-ratelimiter` | 方法级限流能力，用于保护热点接口和控制调用频率。 | 需要防止接口在突发流量下被打爆。 | [vanta-starter-ratelimiter](vanta-starter-ratelimiter/Readme.md) |
| `vanta-starter-messaging` | 消息能力聚合，统一消息模型并按 Kafka、RabbitMQ、RocketMQ 拆分实现。 | 需要统一消息发送模型，并按中间件拆分实现。 | [vanta-starter-messaging](vanta-starter-messaging/Readme.md) |
| `vanta-starter-influxdb` | InfluxDB 时序数据模板，支持写点、写 measurement 和查询 Flux / InfluxQL。 | 需要写入监控、指标或时序业务数据。 | [vanta-starter-influxdb](vanta-starter-influxdb/Readme.md) |
| `vanta-starter-zookeeper` | ZooKeeper 访问模板和分布式锁模板。 | 需要操作 ZooKeeper 节点、监听变化或实现分布式锁。 | [vanta-starter-zookeeper](vanta-starter-zookeeper/Readme.md) |
| `vanta-starter-nacos` | Nacos 能力聚合，分为原生客户端、配置和注册发现。 | 需要原生操作 Nacos 客户端。 | [vanta-starter-nacos](vanta-starter-nacos/Readme.md) |
| `vanta-starter-elasticsearch` | Elasticsearch 客户端模板和索引命名策略。 | 需要统一 ES 索引创建、删除、查询和批量写入。 | [vanta-starter-elasticsearch](vanta-starter-elasticsearch/Readme.md) |
| `vanta-starter-observability` | 观测上下文能力，用于传递和聚合链路字段。 | 需要把 trace、tenant、operator 等字段传递到业务链路中。 | [vanta-starter-observability](vanta-starter-observability/Readme.md) |
| `vanta-starter-lock` | 分布式锁模板，支持 local、redis、zookeeper 和 jdbc 四种实现。 | 需要在单机、集群或数据库场景统一执行锁逻辑。 | [vanta-starter-lock](vanta-starter-lock/Readme.md) |
| `vanta-starter-storage` | 文件存储模板，当前提供本地文件实现。 | 需要统一文件上传、下载、删除和元数据抽象。 | [vanta-starter-storage](vanta-starter-storage/Readme.md) |
| `vanta-starter-scheduler` | 任务调度抽象与本地调度执行器。 | 需要统一定义调度任务命令、执行结果和执行器接口。 | [vanta-starter-scheduler](vanta-starter-scheduler/Readme.md) |

## 使用原则
- 根目录只负责展示能力地图，不建议业务项目直接依赖聚合 POM。
- 业务项目按需引入具体子模块，避免把未使用能力一起带入。
