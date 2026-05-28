# vanta-starter-cache-springcache

## 1. 组件作用
Spring Cache 默认配置模块。

主要公开类型：
- `SpringCacheAutoConfiguration`

## 2. 适用场景
- 需要保留 Spring Cache 默认配置。
- 需要给业务统一缓存过期和空值策略。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-cache-springcache</artifactId>
</dependency>
```

默认配置示例：
```yaml
--- ### Spring Cache 配置
spring.cache:
  redis:
    # 缓存过期时长（单位：毫秒，默认 -1，表示永不过期）
    time-to-live: 7200000
    # 是否允许缓存空值（默认 true，表示允许，可以解决缓存穿透问题）
    cache-null-values: true
```

主要扩展点：
- `SpringCacheAutoConfiguration`
