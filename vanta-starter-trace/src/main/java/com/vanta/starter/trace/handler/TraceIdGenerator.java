package com.vanta.starter.trace.handler;

import com.yomahub.tlog.id.TLogIdGenerator;
import com.yomahub.tlog.id.snowflake.UniqueIdGenerator;


/**
 * TraceIdGenerator 类。
 * <p>该类型属于 链路追踪能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class TraceIdGenerator extends TLogIdGenerator {
    /**
     * 执行 generateTraceId 逻辑。
     * 该方法属于 链路追踪能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String generateTraceId() {
        return String.valueOf(UniqueIdGenerator.generateId());
    }
}
