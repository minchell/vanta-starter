package com.vanta.starter.influxdb.core;

import com.influxdb.client.BucketsApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxQLQueryApi;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.InfluxQLQuery;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import com.influxdb.query.InfluxQLQueryResult;

import java.util.List;

/**
 * Vanta InfluxDB 操作模板。
 *
 * <p>该模板把常用 API 聚合在一个可注入对象上，避免业务代码到处直接依赖
 * InfluxDBClient、WriteApi、QueryApi 等多个底层对象。</p>
 *
 * @param influxDBClient InfluxDB 官方客户端。
 *                       <p>负责创建查询 API、bucket API 和其他底层客户端对象；业务方可以通过自定义 Bean 替换连接策略。</p>
 * @param writeApi       InfluxDB 异步写入 API。
 *                       <p>用于批量写入 Point 或 measurement 对象，批处理参数来自 {@code InfluxDbProperties}。</p>
 */
public record VantaInfluxTemplate(InfluxDBClient influxDBClient, WriteApi writeApi) {

    /**
     * 创建 Vanta InfluxDB 操作模板。
     *
     * @param influxDBClient InfluxDB 官方客户端
     * @param writeApi       InfluxDB 异步写入 API
     */
    public VantaInfluxTemplate {
    }

    /**
     * 创建并读取 Flux 查询 API。
     *
     * @return InfluxDB Flux 查询 API
     */
    public QueryApi getQueryApi() {
        return influxDBClient.getQueryApi();
    }

    /**
     * 创建并读取 InfluxQL 查询 API。
     *
     * @return InfluxDB InfluxQL 查询 API
     */
    public InfluxQLQueryApi getInfluxQLQueryApi() {
        return influxDBClient.getInfluxQLQueryApi();
    }

    /**
     * 创建并读取 bucket 管理 API。
     *
     * @return InfluxDB bucket 管理 API
     */
    public BucketsApi getBucketsApi() {
        return influxDBClient.getBucketsApi();
    }

    /**
     * 写入 InfluxDB Point。
     *
     * @param point InfluxDB 原生 Point
     */
    public void writePoint(Point point) {
        writeApi.writePoint(point);
    }

    /**
     * 写入带 InfluxDB 注解的 measurement 对象。
     *
     * @param measurement 带 @Measurement 和 @Column 的对象
     */
    public void writeMeasurement(Object measurement) {
        writeApi.writeMeasurement(WritePrecision.NS, measurement);
    }

    /**
     * 执行 Flux 查询。
     *
     * @param flux Flux 查询语句
     * @return 查询结果表
     */
    public List<FluxTable> queryFlux(String flux) {
        return getQueryApi().query(flux);
    }

    /**
     * 执行 InfluxQL 查询。
     *
     * @param query    InfluxQL 查询语句
     * @param database InfluxDB 1.x database
     * @return 查询结果
     */
    public InfluxQLQueryResult queryInfluxQl(String query, String database) {
        return getInfluxQLQueryApi().query(new InfluxQLQuery(query, database));
    }
}
