# Vanta Starter 内部搬运标准

本文档定义 `vanta-starter` 的长期定位、模块边界和内部复用标准。仓库目标不是对外社区开源运营，而是让内部项目可以像使用优秀开源 starter 一样，稳定、清晰、可验证地复制和引入能力。

## 目标定位

`vanta-starter` 是内部 Spring Boot 后端 starter 能力仓库，坐标体系保持 `com.vanta:*` 不变。它同时服务两种使用方式：

- Maven 引入：新项目通过 `vanta-starter-dependencies` 统一版本，再按需引入具体 starter。
- 代码搬运：新项目可以从本仓库复制模块、配置、约定和示例，快速形成可维护的后端基础架构。

评判标准不是“是否公开发布”，而是：

- 新项目能一眼知道该引入哪个 starter。
- 默认依赖不会悄悄连接数据库、Redis、MQ、Nacos、ES 等外部资源。
- 每个 starter 的边界、配置、开关和替换点清楚。
- 自动配置行为有测试或架构守卫证明。
- 数据库、Web、日志、安全、缓存等基础约定能长期复用。

## 核心使用模型

新项目优先从 `vanta-starter-web-service` 开始。它是默认可用的最小 Web 服务底座，负责组合：

- `vanta-starter-core`
- `vanta-starter-jackson`
- `vanta-starter-validation`
- `vanta-starter-web`
- `vanta-starter-api-doc`

其它能力必须按需引入，例如数据访问、认证、缓存、日志、加密、消息、限流、锁、文件存储、调度、Nacos、Elasticsearch、InfluxDB、ZooKeeper。

业务项目不应直接依赖根聚合 POM。根 `pom.xml` 只用于本仓库构建、测试和 IDE 导入。

## 默认开关原则

默认开启：

- `vanta-starter-web-service` 的基础 Web 服务能力。
- 不访问外部中间件的基础序列化、校验、响应、异常和接口文档能力。

默认关闭：

- 连接外部系统的 starter，例如 Redis、MQ、Nacos、Elasticsearch、InfluxDB、ZooKeeper、对象存储。
- 会改变业务运行语义的横切能力，例如认证、限流、幂等、接口加密、字段加密、XSS、操作日志。
- 需要项目明确选择实现方式的能力，例如 Spring Cache、JetCache、日志 AOP / 拦截器。

如果一个 starter 会产生外部连接、注册全局拦截器、扫描业务注解、影响请求响应、影响数据库行为，就必须提供显式 `enabled=true` 开关，并在测试中证明默认不生效。

## 模块设计规则

每个 starter 应满足下面规则：

- 只承载一个明确能力，不把多个中间件强耦合在一个模块里。
- 自动配置类使用 `@AutoConfiguration`，并声明在本模块的 `AutoConfiguration.imports` 中。
- 自动配置不使用 `@EnableWebMvc`，避免接管 Spring Boot MVC 默认能力。
- 对外暴露配置属性、核心接口和可替换 Bean，而不是暴露内部实现细节。
- 默认 Bean 使用 `@ConditionalOnMissingBean`，允许业务项目覆盖。
- 可选能力使用 `@ConditionalOnProperty` 或同等条件控制。
- starter 之间只能依赖稳定的底层模块，不能形成循环依赖。
- README 必须说明组件作用、Maven 引入、核心配置、默认行为、边界和替换点。

## 数据库通用约定

数据库能力以 MySQL / PostgreSQL 通用为目标，不追求某一个数据库的便利写法污染业务层。

推荐边界：

- 业务层只依赖 Repository 接口。
- MyBatis-Plus 的 `BaseMapper`、`IService`、`ServiceImpl`、`Wrapper`、`Page` 只能出现在 Repository 实现或 starter 内部。
- Controller、Application Service、Domain Service 不直接出现 `if mysql`、`if postgresql` 分支。
- 分页必须显式配置 `db-type: mysql` 或 `db-type: postgresql`。
- 动态排序、动态列名、动态表名必须通过白名单、枚举或领域查询对象转换。
- JSON、数组、全文检索等强方言能力封装在 Repository 方法中，对上层暴露领域语义。

推荐表字段：

- 主键：`id`
- 创建时间：`created_at`
- 更新时间：`updated_at`
- 逻辑删除：`deleted`

Java 实体保持驼峰字段映射，例如 `createdAt`、`updatedAt`。

## 内部搬运流程

新项目从本仓库搬运或引入能力时，按下面顺序执行：

1. 引入 `vanta-starter-dependencies` 管理版本。
2. 引入 `vanta-starter-web-service` 建立基础 Web 服务。
3. 只为真实需要的能力增加具体 starter。
4. 对每个可选 starter 显式写出 `enabled=true` 和必要连接配置。
5. 对数据库项目引入 `vanta-starter-data-mp`，并遵守 Repository 边界。
6. 复制 `examples/vanta-web-service-demo` 中的启动结构、配置样例和验证方式。
7. 在业务项目中补充与本项目等价的冒烟测试或上下文启动测试。

不要为了“以后可能用到”一次性引入缓存、消息、认证、调度、搜索、时序库等能力。

## 质量门禁

每次调整 starter 后至少执行：

```bash
mvn -q test -DskipITs -Dfile.encoding=UTF-8
```

涉及自动配置时，还应补充或更新：

- 默认不生效测试。
- 显式 enabled 后生效测试。
- Bean 覆盖测试。
- 缺少可选依赖时不启动测试。
- `AutoConfiguration.imports` 和 `@AutoConfiguration` 架构守卫。

涉及数据库约定时，还应补充：

- MySQL / PostgreSQL 分页方言配置测试。
- Repository 边界测试。
- 事务期间禁止切库测试。
- MyBatis-Plus 类型不泄漏到业务层的扫描测试。

## 发布与版本

内部发布以稳定搬运和 Maven 引入为目标：

- 坐标保持 `com.vanta:*`。
- 版本先使用统一 `revision` 管理。
- 对业务项目推荐导入 `vanta-starter-dependencies`，再引入具体 starter。
- 根聚合 POM 不作为业务依赖。
- 发布前必须通过完整 Maven 测试。

## 维护优先级

后续演进按下面顺序推进：

1. 保证 `web-service` 基础服务体验稳定。
2. 补齐可选 starter 的默认关闭和显式开启测试。
3. 强化数据库 MySQL / PostgreSQL 通用约束。
4. 完善示例工程，让它能直接作为新项目模板。
5. 再扩展更多中间件能力。

任何新增能力都应先回答三个问题：

- 它是否属于 starter，而不是业务项目代码？
- 它默认开启是否会产生副作用？
- 它是否有文档、配置、测试和替换点？
