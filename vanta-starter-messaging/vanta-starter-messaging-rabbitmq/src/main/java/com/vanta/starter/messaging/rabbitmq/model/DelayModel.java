package com.vanta.starter.messaging.rabbitmq.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 延迟消息执行计划模型。
 *
 * <p>该对象在各级 TTL 队列之间流转，用于记录原始消息、剩余延迟计划和当前执行步骤。</p>
 */
public class DelayModel implements Serializable {

    /**
     * Java 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 原始业务延迟消息。
     */
    private DelayMessage delayMessage;

    /**
     * 延迟执行计划。
     *
     * <p>每个元素表示一段 TTL 秒数，消费者会按顺序推进。</p>
     */
    private List<Integer> executePlans;

    /**
     * 当前执行步骤下标。
     *
     * <p>值从 1 开始，表示下一次计划队列消费时应读取的执行计划位置。</p>
     */
    private Integer currentStep;

    /**
     * 创建延迟消息执行计划模型。
     *
     * @param delayMessage 原始业务延迟消息
     * @param executePlans 延迟执行计划
     * @param currentStep  当前执行步骤下标
     */
    public DelayModel(DelayMessage delayMessage, List<Integer> executePlans, Integer currentStep) {
        this.delayMessage = delayMessage;
        this.executePlans = executePlans;
        this.currentStep = currentStep;
    }

    /**
     * 获取原始业务延迟消息。
     *
     * @return 原始业务延迟消息
     */
    public DelayMessage getDelayMessage() {
        return delayMessage;
    }

    /**
     * 设置原始业务延迟消息。
     *
     * @param delayMessage 原始业务延迟消息
     */
    public void setDelayMessage(DelayMessage delayMessage) {
        this.delayMessage = delayMessage;
    }

    /**
     * 获取延迟执行计划。
     *
     * @return 延迟执行计划
     */
    public List<Integer> getExecutePlans() {
        return executePlans;
    }

    /**
     * 设置延迟执行计划。
     *
     * @param executePlans 延迟执行计划
     */
    public void setExecutePlans(List<Integer> executePlans) {
        this.executePlans = executePlans;
    }

    /**
     * 获取当前执行步骤下标。
     *
     * @return 当前执行步骤下标
     */
    public Integer getCurrentStep() {
        return currentStep;
    }

    /**
     * 设置当前执行步骤下标。
     *
     * @param currentStep 当前执行步骤下标
     */
    public void setCurrentStep(Integer currentStep) {
        this.currentStep = currentStep;
    }
}
