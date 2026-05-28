# vanta-starter-log-aop

## 1. 组件作用
AOP 实现模块，把能力织入业务方法调用链。

主要公开类型：
- `LogAutoConfiguration`
- `LogAspect`
- `AccessLogAspect`
- `AopLogHandler`

## 2. 适用场景
- 需要把能力织入方法调用链。
- 需要通过切面实现业务无感接入。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-log-aop</artifactId>
</dependency>
```

主要扩展点：
- `LogAutoConfiguration`
- `LogAspect`
- `AccessLogAspect`
- `AopLogHandler`
