package com.vanta.starter.messaging.rabbitmq.constant;

/**
 * 延迟消息拓扑命名模板。
 *
 * <p>所有模板都以业务名称作为第一个占位符，避免多个业务使用同一个 RabbitMQ vhost 时资源名冲突。</p>
 */
public class DelayConstants {

    /**
     * 延迟交换机名称模板。
     */
    public final static String DELAY_EXCHANGE_FMT = "%s.second.delay.exchange";

    /**
     * 分段 TTL 延迟队列名称模板。
     */
    public final static String DELAY_QUEUE_FMT = "%s.%d.second.delay.queue";

    /**
     * 分段 TTL 延迟队列路由键模板。
     */
    public final static String DELAY_ROUTING_FMT = "%s_%d_second_delay_routing";

    /**
     * 计划交换机名称模板。
     */
    public final static String PLAN_EXCHANGE_FMT = "%s.second.plan.exchange";

    /**
     * 计划队列名称模板。
     */
    public final static String PLAN_QUEUE_FMT = "%s.second.plan.queue";

    /**
     * 计划队列路由键模板。
     */
    public final static String PLAN_ROUTING_FMT = "%s_second_plan_routing";

    /**
     * 完成交换机名称模板。
     */
    public final static String FINISH_EXCHANGE_FMT = "%s.second.finish.exchange";

    /**
     * 完成队列名称模板。
     */
    public final static String FINISH_QUEUE_FMT = "%s.second.finish.queue";

    /**
     * 完成队列路由键模板。
     */
    public final static String FINISH_ROUTING_FMT = "%s_second_finish_routing";

}
