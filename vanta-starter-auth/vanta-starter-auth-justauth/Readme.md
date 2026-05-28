# vanta-starter-auth-justauth

## 1. 组件作用
JustAuth 第三方登录接入模块。

主要公开类型：
- `JustAuthAutoConfiguration`
- `JustAuthProperties`
- `JustAuthHttpProperties`
- `JustAuthCacheProperties`
- `JustAuthExtendProperties`
- `AuthRequestFactory`
- `CacheType`
- `RedisAuthStateCache`

## 2. 适用场景
- 需要接入第三方登录平台。
- 需要对缓存、HTTP 和扩展配置做统一封装。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-auth-justauth</artifactId>
</dependency>
```

主要扩展点：
- `JustAuthAutoConfiguration`
