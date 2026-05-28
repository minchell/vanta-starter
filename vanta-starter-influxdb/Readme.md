# vanta-starter-influxdb

## 1. 组件作用
InfluxDB 时序数据模板，支持写点、写 measurement 和查询 Flux / InfluxQL。

主要公开类型：
- `InfluxDbAutoConfiguration`
- `InfluxDbProperties`
- `VantaInfluxTemplate`
- `NanoClock`
- `InfluxQueryBuilder`
- `InfluxMeasurementMapper`

## 2. 适用场景
- 需要写入监控、指标或时序业务数据。
- 需要统一 InfluxDB 客户端和写入方式。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-influxdb</artifactId>
</dependency>
```

主要扩展点：
- `InfluxDbAutoConfiguration`
- `VantaInfluxTemplate`
