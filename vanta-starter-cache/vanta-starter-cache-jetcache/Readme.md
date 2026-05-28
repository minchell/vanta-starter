# vanta-starter-cache-jetcache

## 1. 组件作用
JetCache 接入模块。

主要公开类型：
- `JetCacheAutoConfiguration`

## 2. 适用场景
- 需要统一 JetCache 默认接入方式。
- 需要保留本地缓存和远程缓存的一致配置入口。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-cache-jetcache</artifactId>
</dependency>
```

默认配置示例：
```yaml
--- ### JetCache 配置（https://github.com/alibaba/jetcache/blob/master/docs/CN/Config.md）
jetcache:
  # 统计间隔（默认 0，表示不统计）
  statIntervalMinutes: 0
  # jetcache-anno 把 cacheName 作为远程缓存key前缀，
  # 2.4.3 以前的版本总是把 areaName 加在 cacheName 中，因此 areaName 也出现在 key 前缀中，
  # 2.4.4 以后可以配置，为了保持远程 key 兼容默认值为 true，但是新项目的话 false 更合理些，2.7 默认值已改为 false。
  areaInCacheName: false
```

主要扩展点：
- `JetCacheAutoConfiguration`
