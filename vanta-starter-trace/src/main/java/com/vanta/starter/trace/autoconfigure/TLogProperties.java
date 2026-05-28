package com.vanta.starter.trace.autoconfigure;


/**
 * TLogProperties 类。
 * <p>该类型属于 日志能力，负责承载 Spring Boot 配置绑定参数，并为自动配置提供可读的开关和连接参数。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class TLogProperties {

    /**
     * 日志标签模板
     */
    private String pattern;

    /**
     * 自动打印调用参数和时间
     */
    private Boolean enableInvokeTimePrint;

    /**
     * 自定义 TraceId 生成器
     */
    private String idGenerator;

    /**
     * MDC 模式
     */
    private Boolean mdcEnable;

    /**
     * 读取 Pattern 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * 设置 Pattern 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param pattern pattern 参数，调用方应传入与 日志能力 场景匹配的有效值
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 读取 Enable Invoke Time Print 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public Boolean getEnableInvokeTimePrint() {
        return enableInvokeTimePrint;
    }

    /**
     * 设置 Enable Invoke Time Print 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param enableInvokeTimePrint enableInvokeTimePrint 参数，调用方应传入与 日志能力 场景匹配的有效值
     */
    public void setEnableInvokeTimePrint(Boolean enableInvokeTimePrint) {
        this.enableInvokeTimePrint = enableInvokeTimePrint;
    }

    /**
     * 读取 Id Generator 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public String getIdGenerator() {
        return idGenerator;
    }

    /**
     * 设置 Id Generator 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param idGenerator idGenerator 参数，调用方应传入与 日志能力 场景匹配的有效值
     */
    public void setIdGenerator(String idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 读取 Mdc Enable 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public Boolean getMdcEnable() {
        return mdcEnable;
    }

    /**
     * 设置 Mdc Enable 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param mdcEnable mdcEnable 参数，调用方应传入与 日志能力 场景匹配的有效值
     */
    public void setMdcEnable(Boolean mdcEnable) {
        this.mdcEnable = mdcEnable;
    }
}
