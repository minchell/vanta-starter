package com.vanta.starter.core.autoconfigure.threadpool;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Spring 任务线程池扩展配置属性。
 * <p>
 * 该配置绑定 {@code spring.task.*} 下的扩展字段，只补充 Spring Boot 线程池配置中缺少的拒绝策略等能力。
 * 默认值采用调用方线程执行策略，避免任务被静默丢弃，也不会主动创建任何远程资源。
 * </p>
 */
@ConfigurationProperties("spring.task")
public class ThreadPoolExtensionProperties {

    /**
     * 异步任务扩展配置属性
     */
    private ExecutorExtensionProperties execution = new ExecutorExtensionProperties();

    /**
     * 调度任务扩展配置属性
     */
    private SchedulerExtensionProperties scheduling = new SchedulerExtensionProperties();

    /**
     * 获取异步任务扩展配置属性。
     *
     * @return 异步任务扩展配置属性
     */
    public ExecutorExtensionProperties getExecution() {
        return execution;
    }

    /**
     * 设置异步任务扩展配置属性。
     *
     * @param execution 异步任务扩展配置属性
     */
    public void setExecution(ExecutorExtensionProperties execution) {
        this.execution = execution;
    }

    /**
     * 获取调度任务扩展配置属性。
     *
     * @return 调度任务扩展配置属性
     */
    public SchedulerExtensionProperties getScheduling() {
        return scheduling;
    }

    /**
     * 设置调度任务扩展配置属性。
     *
     * @param scheduling 调度任务扩展配置属性
     */
    public void setScheduling(SchedulerExtensionProperties scheduling) {
        this.scheduling = scheduling;
    }

    /**
     * 异步任务扩展配置属性
     */
    public static class ExecutorExtensionProperties {
        /**
         * 拒绝策略
         */
        private ThreadPoolExecutorRejectedPolicy rejectedPolicy = ThreadPoolExecutorRejectedPolicy.CALLER_RUNS;

        /**
         * 获取拒绝策略。
         *
         * @return 拒绝策略
         */
        public ThreadPoolExecutorRejectedPolicy getRejectedPolicy() {
            return rejectedPolicy;
        }

        /**
         * 设置拒绝策略。
         *
         * @param rejectedPolicy 拒绝策略
         */
        public void setRejectedPolicy(ThreadPoolExecutorRejectedPolicy rejectedPolicy) {
            this.rejectedPolicy = rejectedPolicy;
        }
    }

    /**
     * 调度任务扩展配置属性
     */
    public static class SchedulerExtensionProperties {
        /**
         * 拒绝策略
         */
        private ThreadPoolExecutorRejectedPolicy rejectedPolicy = ThreadPoolExecutorRejectedPolicy.CALLER_RUNS;

        /**
         * 获取拒绝策略。
         *
         * @return 拒绝策略
         */
        public ThreadPoolExecutorRejectedPolicy getRejectedPolicy() {
            return rejectedPolicy;
        }

        /**
         * 设置拒绝策略。
         *
         * @param rejectedPolicy 拒绝策略
         */
        public void setRejectedPolicy(ThreadPoolExecutorRejectedPolicy rejectedPolicy) {
            this.rejectedPolicy = rejectedPolicy;
        }
    }
}
