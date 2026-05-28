# vanta-starter-data

## 1. 组件作用
数据访问能力聚合，提供查询抽象和 MyBatis Plus 扩展。

## 2. 适用场景
- 需要统一查询表达式、SQL 工具和 MyBatis Plus 扩展。
- 需要在数据层保留可复用的基础类型和拦截器能力。

## 3. 接入方式
聚合模块本身通常不作为业务运行时依赖；业务项目应直接按需引入下面的子模块。

子模块：
- [vanta-starter-data-core](vanta-starter-data-core/Readme.md)
- [vanta-starter-data-mp](vanta-starter-data-mp/Readme.md)
