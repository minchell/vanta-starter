# vanta-starter-excel-fastexcel

## 1. 组件作用
可复用的 Starter 模块。

主要公开类型：
- `ExcelUtils`
- `ExcelListConverter`
- `ExcelBaseEnumConverter`
- `ExcelBigNumberConverter`

## 2. 适用场景
- 需要作为一个可复用的 Starter 能力被单独引入。
- 需要保持与业务代码的边界清晰。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-excel-fastexcel</artifactId>
</dependency>
```

主要扩展点：
- `ExcelUtils`
