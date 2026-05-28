# vanta-starter-core

## 1. 组件作用
基础能力底座，提供应用元信息、线程池扩展、异常体系、通用工具类和表达式工具。

主要公开类型：
- `ThreadPoolAutoConfiguration`
- `ApplicationAutoConfiguration`
- `PropertiesConstants`
- `ApplicationProperties`
- `ThreadPoolExtensionProperties`
- `TemplateUtils`
- `IpUtils`
- `MapUtils`

配置前缀：
- `application`
- `spring.task`

## 2. 适用场景
- 需要统一应用名称、版本、联系人、生产环境标记等基础元信息。
- 需要扩展 Spring 任务线程池拒绝策略，但不想引入额外远程依赖。
- 需要复用字符串、集合、日期、反射、模板、树结构和表达式工具。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-core</artifactId>
</dependency>
```

主要扩展点：
- `ThreadPoolAutoConfiguration`
- `ApplicationAutoConfiguration`
- `TemplateUtils`
- `IpUtils`
- `MapUtils`
