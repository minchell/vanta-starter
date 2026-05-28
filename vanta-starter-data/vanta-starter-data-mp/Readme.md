# vanta-starter-data-mp

## 组件作用

`vanta-starter-data-mp` 是 MyBatis-Plus 数据访问治理 starter。它不是给业务层提供 CRUD 便利，而是把 MyBatis-Plus 限制在 Repository 实现内部，并以 MySQL / PostgreSQL 通用约定为目标统一处理：

- `ASSIGN_ID` 分布式主键
- `BaseDO` 审计字段填充
- 分页对象转换
- 乐观锁、阻断全表更新/删除
- Repository 级数据源路由
- 事务期间禁止切库
- CosId ID 生成器接入

适合的场景：

- 项目采用 `controller -> application service -> domain service -> repository -> mapper` 分层。
- 业务层不允许直接接触 `IService`、`ServiceImpl`、`BaseMapper`、`Wrapper`、`Page`。
- 需要统一分布式 ID 策略，并允许切换 CosId 或自定义 `IdentifierGenerator`。
- 需要把数据库访问能力收敛到 Repository，防止 SQL 污染和事务混乱。

## Maven 引入

```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-data-mp</artifactId>
</dependency>
```

## 推荐配置

MySQL：

```yaml
mybatis-plus:
  extension:
    enabled: true
    mapper-package: com.example.**.mapper
    id-generator:
      type: cosid
    pagination:
      enabled: true
      db-type: mysql
      overflow: false
      max-limit: 500
    optimistic-locker-enabled: true
    block-attack-plugin-enabled: true
  configuration:
    map-underscore-to-camel-case: true
    auto-mapping-unknown-column-behavior: NONE
  global-config:
    db-config:
      id-type: ASSIGN_ID
      logic-delete-field: deleted
      logic-not-delete-value: 0
      logic-delete-value: 1

cosid:
  enabled: true
  namespace: vanta-admin
  snowflake:
    enabled: true
    share:
      enabled: true
  machine:
    enabled: true
    distributor:
      type: redis
```

PostgreSQL 只需要把分页方言切换为 `postgresql`，其余 Repository 边界约束不变：

```yaml
mybatis-plus:
  extension:
    enabled: true
    mapper-package: com.example.**.mapper
    pagination:
      enabled: true
      db-type: postgresql
      overflow: false
      max-limit: 500
    optimistic-locker-enabled: true
    block-attack-plugin-enabled: true
```

## MySQL / PostgreSQL 通用约束

- 业务层只能依赖 Repository 接口，不能直接依赖 `BaseMapper`、`IService`、`ServiceImpl`、`Wrapper` 或 MyBatis-Plus `Page`。
- 动态排序、动态列名、动态表名必须使用白名单、枚举或领域查询对象转换，不能拼接用户原始输入。
- 方言差异集中在 `DatabaseType` / MyBatis-Plus `DbType` / Repository 实现内部，Controller 和 Application Service 不允许出现 `if mysql` / `if postgresql` 分支。
- 分页必须显式配置 `db-type: mysql` 或 `db-type: postgresql`，避免运行时反复探测数据库类型。
- 通用字段建议使用下划线命名：`id`、`created_at`、`updated_at`、`deleted`，并保持 Java 实体驼峰映射。
- JSON、数组、全文搜索等强方言能力应封装在 Repository 方法中，对业务层暴露领域语义方法。

## ID 生成说明

- 实体主键统一使用 `@TableId(type = IdType.ASSIGN_ID)`。
- `mybatis-plus.extension.id-generator.type=cosid` 时，starter 会把 MyBatis-Plus ID 生成接到 CosId。
- `cosid.machine.distributor.type=redis` 时，CosId 会通过 Redis 分配机器号，适合多实例部署。
- 测试环境可以关闭 `cosid.enabled`，让默认单元测试不依赖 Redis。

## BaseDO 规范

```java
public class BaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

说明：

- `createdAt` 只在插入时填充。
- `updatedAt` 在插入和更新时都填充。
- `BaseDO` 不内置业务用户体系字段，业务审计字段应通过扩展 `MetaObjectHandler` 或独立基础模型补充。

## Repository 内部分页示例

```java
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @RepositoryShard(RepositoryShardType.READ)
    public PageResult<UserDO> findUsers(UserPageQuery query) {
        Page<UserDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .like(StrUtil.isNotBlank(query.getKeyword()), UserDO::getUsername, query.getKeyword())
                .orderByDesc(UserDO::getCreatedAt);
        return PageConverter.from(userMapper.selectPage(page, wrapper));
    }
}
```

上面的 `Page`、`LambdaQueryWrapper`、`Mapper` 只能出现在 Repository 实现内部。

## 适配边界

`vanta-starter-data-mp` 不提供下面这些能力：

- 业务层 CRUD 门面
- `IService` / `ServiceImpl`
- 向上暴露 `Wrapper`
- 向上暴露 `Page`
- 向业务层暴露数据源切换注解

它只负责把 MyBatis-Plus 变成一个受控的数据访问基础设施。
