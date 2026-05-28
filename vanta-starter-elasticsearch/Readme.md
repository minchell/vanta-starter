# vanta-starter-elasticsearch

## 1. 组件作用
Elasticsearch 客户端模板和索引命名策略。

主要公开类型：
- `ElasticsearchAutoConfiguration`
- `ElasticsearchProperties`
- `VantaElasticsearchTemplate`
- `BulkWriter`
- `IndexNameStrategy`

## 2. 适用场景
- 需要统一 ES 索引创建、删除、查询和批量写入。
- 需要把索引前缀和命名规则收敛到一处。

## 3. 接入方式
Maven 依赖：
```xml
<dependency>
  <groupId>com.vanta</groupId>
  <artifactId>vanta-starter-elasticsearch</artifactId>
</dependency>
```

主要扩展点：
- `ElasticsearchAutoConfiguration`
- `VantaElasticsearchTemplate`
