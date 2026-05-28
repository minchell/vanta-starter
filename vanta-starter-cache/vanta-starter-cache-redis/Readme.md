# vanta-starter-cache-redis

## 组件作用

`vanta-starter-cache-redis` 是 Redis 接入 starter，负责把 Spring Data Redis、RedissonClient、RedisTemplate、StringRedisTemplate 和常用 Redis 工具封装成可独立引入的基础设施能力。

该模块适合以下场景：

- 业务服务需要统一 RedisTemplate、StringRedisTemplate 的序列化方式。
- 业务服务需要使用 Redisson 承载分布式锁、限流、幂等、Sa-Token Redis DAO、CosId Redis 机器号分配器等能力。
- 项目希望 Redis 能力由明确配置开关控制，测试环境或未接入 Redis 的服务可以关闭远程副作用。
- 业务方希望通过自定义 `RedissonClient` 或 `RedisConnectionFactory` 替换 starter 默认实现。

该模块不会承载任何 具体业务项目语义，也不会默认创建业务缓存结构。

## Maven 引入

```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-cache-redis</artifactId>
</dependency>
```

## 单机 Redis 配置

```yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password:
      database: 0
    redisson:
      enabled: true
      mode: SINGLE
      key-prefix: demo-service
      single-server-config:
        address: redis://127.0.0.1:6379
        password:
```

说明：

- `spring.data.redisson.enabled=true` 时才会创建 `RedissonClient` 与 `RedissonConnectionFactory`。
- `spring.data.redisson.enabled=false` 时不会创建远程 Redis 客户端，适合默认单元测试。
- `key-prefix` 会通过 Redisson `NameMapper` 给 key 增加项目级前缀，减少多服务共用 Redis 时的 key 冲突。
- `single-server-config.address` 为空时，starter 会根据 `spring.data.redis.host`、`spring.data.redis.port` 和 SSL 配置推导地址。

## Cluster 配置

```yaml
spring:
  data:
    redis:
      password: redis-password
      cluster:
        nodes:
          - 10.0.0.11:6379
          - 10.0.0.12:6379
          - 10.0.0.13:6379
    redisson:
      enabled: true
      mode: CLUSTER
      key-prefix: demo-service
```

未显式配置 `cluster-servers-config.node-addresses` 时，starter 会读取 `spring.data.redis.cluster.nodes` 生成 Redisson Cluster 节点地址。

## Sentinel 配置

```yaml
spring:
  data:
    redis:
      password: redis-password
      sentinel:
        master: mymaster
        nodes:
          - 10.0.0.21:26379
          - 10.0.0.22:26379
          - 10.0.0.23:26379
    redisson:
      enabled: true
      mode: SENTINEL
      key-prefix: demo-service
```

未显式配置 `sentinel-servers-config.sentinel-addresses` 时，starter 会读取 `spring.data.redis.sentinel.nodes` 生成 Redisson Sentinel 地址。

## 测试环境关闭示例

```yaml
spring:
  data:
    redisson:
      enabled: false
```

关闭后不会创建 `RedissonClient`，依赖 Redis 的能力也应在各自 starter 或业务配置中同步关闭，例如 Sa-Token Redis DAO、CosId Redis 机器号分配器、分布式锁 Redis 实现等。

## 可替换 Bean

业务项目可以声明以下 Bean 替换默认实现：

- `RedissonClient`
- `RedisConnectionFactory`
- `RedisTemplate<String, Object>`
- `StringRedisTemplate`

starter 默认 Bean 均使用缺省 Bean 条件或明确开关控制，避免强行接管业务项目的 Redis 基础设施。
