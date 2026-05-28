package com.vanta.starter.messaging.rabbitmq.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.messaging.rabbitmq.constant.DelayConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * RabbitMQ 延迟消息能力配置。
 *
 * <p>配置前缀来自 {@link PropertiesConstants#RABBITMQ_DELAY}。该能力通过多级 TTL 队列和死信交换机模拟延迟投递，
 * 只有显式开启后才会创建延迟交换机、计划队列和完成队列。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.RABBITMQ_DELAY)
public class RabbitMqDelayProperties {

    /**
     * 是否启用延迟消息功能
     */
    private boolean enabled = false;

    /**
     * 延迟任务名称（用于区分不同的延迟业务）
     */
    private String name = "default";

    /**
     * 自定义延迟级别（秒）
     */
    private List<Integer> delayLevels = List.of(1, 2, 5, 10, 30, 60, 120, 300, 600, 1800, 3600, 10800);

    /**
     * 生成延迟交换机名称。
     *
     * @return 基于 {@link #name} 拼接出的延迟交换机名称
     */
    public String getDelayExchange() {
        return DelayConstants.DELAY_EXCHANGE_FMT.formatted(this.name);
    }

    /**
     * 生成计划交换机名称。
     *
     * <p>延迟队列消息到期后会通过死信交换机进入该交换机，再进入计划队列等待业务消费者处理。</p>
     *
     * @return 基于 {@link #name} 拼接出的计划交换机名称
     */
    public String getPlanExchange() {
        return DelayConstants.PLAN_EXCHANGE_FMT.formatted(this.name);
    }

    /**
     * 生成完成交换机名称。
     *
     * @return 基于 {@link #name} 拼接出的完成交换机名称
     */
    public String getFinishExchange() {
        return DelayConstants.FINISH_EXCHANGE_FMT.formatted(this.name);
    }

    /**
     * 生成指定 TTL 秒数对应的延迟队列名称。
     *
     * @param ttlSecond 延迟级别，单位为秒
     * @return 基于业务名称和 TTL 秒数拼接出的延迟队列名称
     */
    public String getDelayQueue4Second(int ttlSecond) {
        return DelayConstants.DELAY_QUEUE_FMT.formatted(this.name, ttlSecond);
    }

    /**
     * 生成计划队列名称。
     *
     * @return 基于 {@link #name} 拼接出的计划队列名称
     */
    public String getPlanQueue() {
        return DelayConstants.PLAN_QUEUE_FMT.formatted(this.name);
    }

    /**
     * 生成完成队列名称。
     *
     * @return 基于 {@link #name} 拼接出的完成队列名称
     */
    public String getFinishQueue() {
        return DelayConstants.FINISH_QUEUE_FMT.formatted(this.name);
    }

    /**
     * 生成计划队列路由键。
     *
     * @return 基于 {@link #name} 拼接出的计划路由键
     */
    public String getPlanRouting() {
        return DelayConstants.PLAN_ROUTING_FMT.formatted(this.name);
    }

    /**
     * 生成完成队列路由键。
     *
     * @return 基于 {@link #name} 拼接出的完成路由键
     */
    public String getFinishRouting() {
        return DelayConstants.FINISH_ROUTING_FMT.formatted(this.name);
    }

    /**
     * 生成指定 TTL 秒数对应的延迟路由键。
     *
     * @param ttlSecond 延迟级别，单位为秒
     * @return 基于业务名称和 TTL 秒数拼接出的延迟路由键
     */
    public String getDelayRouting4Second(int ttlSecond) {
        return DelayConstants.DELAY_ROUTING_FMT.formatted(this.name, ttlSecond);
    }

    /**
     * 获取是否启用延迟消息功能。
     *
     * @return 是否启用延迟消息功能
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用延迟消息功能。
     *
     * @param enabled 是否启用延迟消息功能
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取延迟任务名称（用于区分不同的延迟业务）。
     *
     * @return 延迟任务名称（用于区分不同的延迟业务）
     */
    public String getName() {
        return name;
    }

    /**
     * 设置延迟任务名称（用于区分不同的延迟业务）。
     *
     * @param name 延迟任务名称（用于区分不同的延迟业务）
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取自定义延迟级别（秒）。
     *
     * @return 自定义延迟级别（秒）
     */
    public List<Integer> getDelayLevels() {
        return delayLevels;
    }

    /**
     * 设置自定义延迟级别（秒）。
     *
     * @param delayLevels 自定义延迟级别（秒）
     */
    public void setDelayLevels(List<Integer> delayLevels) {
        this.delayLevels = delayLevels;
    }
}
