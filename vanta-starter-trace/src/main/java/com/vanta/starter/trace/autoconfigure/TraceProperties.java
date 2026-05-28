package com.vanta.starter.trace.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;


/**
 * TraceProperties 类。
 * <p>该类型属于 链路追踪能力，负责承载 Spring Boot 配置绑定参数，并为自动配置提供可读的开关和连接参数。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@ConfigurationProperties(PropertiesConstants.TRACE)
public class TraceProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 链路 ID 名称
     */
    private String traceIdName = "traceId";

    /**
     * TLog 配置
     */
    @NestedConfigurationProperty
    private TLogProperties tlog;

    /**
     * 读取 Enabled 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置 Enabled 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param enabled enabled 参数，调用方应传入与 链路追踪能力 场景匹配的有效值
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 读取 Trace Id Name 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public String getTraceIdName() {
        return traceIdName;
    }

    /**
     * 设置 Trace Id Name 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param traceIdName traceIdName 参数，调用方应传入与 链路追踪能力 场景匹配的有效值
     */
    public void setTraceIdName(String traceIdName) {
        this.traceIdName = traceIdName;
    }

    /**
     * 读取 Tlog 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public TLogProperties getTlog() {
        return tlog;
    }

    /**
     * 设置 Tlog 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param tlog tlog 参数，调用方应传入与 链路追踪能力 场景匹配的有效值
     */
    public void setTlog(TLogProperties tlog) {
        this.tlog = tlog;
    }
}
