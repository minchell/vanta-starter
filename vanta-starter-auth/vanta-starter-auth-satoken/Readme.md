# vanta-starter-auth-satoken

## 1. 组件作用
Sa-Token 扩展模块。

主要公开类型：
- `SaTokenAutoConfiguration`
- `SaTokenDaoProperties`
- `SaTokenSecurityProperties`
- `SaTokenExtensionProperties`
- `SaTokenDaoConfiguration`
- `SaTokenDaoType`

配置前缀：
- `sa-token.extension`

## 2. 适用场景
- 需要扩展 Sa-Token 登录态和 DAO 策略。
- 需要在 JWT 和传统 DAO 之间切换。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-auth-satoken</artifactId>
</dependency>
```

默认配置示例：
```yaml
--- ### Sa-Token 配置（https://sa-token.cc/doc.html#/use/config）
sa-token:
  # token 前缀（例如填写 Bearer，实际传参 token 键: Bearer xxxx-xxxx-xxxx-xxxx）
  token-prefix: Bearer
  # 是否尝试从 请求体 里读取 Token
  is-read-body: true
  # 是否尝试从 header 里读取 Token
  is-read-header: true
  # 是否尝试从 cookie 里读取 Token（此值为 false 后，StpUtil.login(id) 登录时也不会再往前端注入 Cookie，适合前后端分离模式）
  is-read-cookie: false
```

主要扩展点：
- `SaTokenAutoConfiguration`
