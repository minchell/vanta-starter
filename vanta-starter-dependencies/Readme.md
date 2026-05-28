# vanta-starter-dependencies

## 1. 组件作用

对外依赖管理 POM，统一承载 `vanta-starter` 仓库的版本、仓库、插件管理和通用依赖约定。

Maven 坐标：

```xml
<groupId>com.vanta</groupId>
<artifactId>vanta-starter-dependencies</artifactId>
<version>0.0.1-SNAPSHOT</version>
<type>pom</type>
```

## 2. 适用场景

- 业务项目需要统一导入 Vanta starter 的版本管理。
- starter 模块开发需要继承统一 Java 版本、编译插件和测试插件配置。
- 发布前需要检查第三方依赖版本和 `com.vanta` starter 模块版本清单。

## 3. 接入方式

业务项目通过 `dependencyManagement` 导入：

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.vanta</groupId>
      <artifactId>vanta-starter-dependencies</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

本 POM 会导入 `vanta-starter-bom`，业务项目再按需声明具体 starter 依赖即可。
