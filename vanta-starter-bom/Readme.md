# vanta-starter-bom

## 1. 组件作用

starter 组件 BOM，统一声明第三方依赖版本和 `com.vanta` starter 模块版本。

Maven 坐标：

```xml
<groupId>com.vanta</groupId>
<artifactId>vanta-starter-bom</artifactId>
<version>0.0.1-SNAPSHOT</version>
<type>pom</type>
```

## 2. 适用场景

- `vanta-starter-dependencies` 导入本 BOM，向业务项目提供统一依赖版本。
- 发布 starter 前核对各模块对外版本是否一致。
- 需要单独查看 Vanta starter 组件清单和第三方依赖版本。

## 3. 使用原则

优先让业务项目导入 `vanta-starter-dependencies`，由它间接导入本 BOM；只有在确实只需要 BOM 版本清单、不需要父 POM 插件约定时，才单独导入本模块。
