# vanta-starter-cache

## 1. 组件作用
缓存能力聚合，覆盖 Spring Cache、Redis 和 JetCache。

## 2. 适用场景
- 需要统一本地缓存、Redis 缓存和 JetCache 接入方式。
- 需要把缓存策略切换控制在同一套前缀和开关里。

## 3. 接入方式
聚合模块本身通常不作为业务运行时依赖；业务项目应直接按需引入下面的子模块。

子模块：
- [vanta-starter-cache-redis](vanta-starter-cache-redis/Readme.md)
- [vanta-starter-cache-springcache](vanta-starter-cache-springcache/Readme.md)
- [vanta-starter-cache-jetcache](vanta-starter-cache-jetcache/Readme.md)
