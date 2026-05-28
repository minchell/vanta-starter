# vanta-starter-lock

## 1. 组件作用
分布式锁模板，支持 local、redis、zookeeper 和 jdbc 四种实现。

主要公开类型：
- `LockAutoConfiguration`
- `LockProperties`
- `JdbcLockTemplate`
- `RedisLockTemplate`
- `LocalJvmLockTemplate`
- `ZookeeperLockTemplate`
- `DistributedLockTemplate`
- `LockExecutionResult`

## 2. 适用场景
- 需要在单机、集群或数据库场景统一执行锁逻辑。
- 需要在没有外部中间件时也能通过 local 锁完成默认单测。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-lock</artifactId>
</dependency>
```

主要扩展点：
- `LockAutoConfiguration`
- `JdbcLockTemplate`
- `RedisLockTemplate`
- `LocalJvmLockTemplate`
- `ZookeeperLockTemplate`
- `DistributedLockTemplate`
