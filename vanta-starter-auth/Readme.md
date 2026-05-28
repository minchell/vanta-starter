# vanta-starter-auth

## 1. 组件作用
认证授权能力聚合，包含 Sa-Token 扩展与 JustAuth 第三方登录。

## 2. 适用场景
- 需要统一登录态、第三方登录和认证扩展。
- 需要在 Sa-Token 基础上保留可替换的 DAO 和安全策略。

## 3. 接入方式
聚合模块本身通常不作为业务运行时依赖；业务项目应直接按需引入下面的子模块。

子模块：
- [vanta-starter-auth-satoken](vanta-starter-auth-satoken/Readme.md)
- [vanta-starter-auth-justauth](vanta-starter-auth-justauth/Readme.md)
