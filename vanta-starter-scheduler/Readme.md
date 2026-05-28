# vanta-starter-scheduler

## 1. 组件作用
任务调度抽象与本地调度执行器。

主要公开类型：
- `SchedulerAutoConfiguration`
- `SchedulerProperties`
- `ScheduledTaskResult`
- `ScheduledTaskCommand`
- `ScheduledTaskExecutor`
- `LocalScheduledTaskExecutor`

## 2. 适用场景
- 需要统一定义调度任务命令、执行结果和执行器接口。
- 需要在项目中保留一个最小可运行的本地调度实现。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-scheduler</artifactId>
</dependency>
```

主要扩展点：
- `SchedulerAutoConfiguration`
