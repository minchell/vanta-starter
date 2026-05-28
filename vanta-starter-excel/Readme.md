# vanta-starter-excel

## 1. 组件作用
Excel 能力聚合，支持 FastExcel 和 POI 两种实现。

## 2. 适用场景
- 需要按场景在 FastExcel 与 POI 之间切换。
- 需要复用导入导出模型和工具类。

## 3. 接入方式
聚合模块本身通常不作为业务运行时依赖；业务项目应直接按需引入下面的子模块。

子模块：
- [vanta-starter-excel-core](vanta-starter-excel-core/Readme.md)
- [vanta-starter-excel-fastexcel](vanta-starter-excel-fastexcel/Readme.md)
- [vanta-starter-excel-poi](vanta-starter-excel-poi/Readme.md)
