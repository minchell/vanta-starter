package com.vanta.starter.core.autoconfigure.threadpool;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池拒绝策略枚举。
 * <p>
 * 该枚举把配置文件中的可读选项转换为 JDK 标准 {@link RejectedExecutionHandler}。
 * 业务方可以通过配置选择不同策略，也可以绕过本 starter 自行提供线程池 Bean 完成替换。
 * </p>
 */
public enum ThreadPoolExecutorRejectedPolicy {

    /**
     * ThreadPoolTaskExecutor 默认的拒绝策略，不执行新任务，直接抛出 RejectedExecutionException 异常
     */
    ABORT {
        /**
         * 创建直接抛出异常的拒绝处理器。
         *
         * @return JDK 标准的终止拒绝策略处理器。
         */
        @Override
        public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.AbortPolicy();
        }
    },

    /**
     * 提交的任务在执行被拒绝时，会由提交任务的线程去执行
     */
    CALLER_RUNS {
        /**
         * 创建由提交任务线程回退执行任务的拒绝处理器。
         *
         * @return JDK 标准的调用方执行拒绝策略处理器。
         */
        @Override
        public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    },

    /**
     * 不执行新任务，也不抛出异常
     */
    DISCARD {
        /**
         * 创建静默丢弃新任务的拒绝处理器。
         *
         * @return JDK 标准的丢弃拒绝策略处理器。
         */
        @Override
        public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.DiscardPolicy();
        }
    },

    /**
     * 拒绝新任务，但是会抛弃队列中最老的任务，然后尝试再次提交新任务
     */
    DISCARD_OLDEST {
        /**
         * 创建丢弃队列最老任务并重试提交的拒绝处理器。
         *
         * @return JDK 标准的丢弃最老任务拒绝策略处理器。
         */
        @Override
        public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        }
    };

    /**
     * 获取拒绝处理器
     *
     * @return 拒绝处理器
     */
    public abstract RejectedExecutionHandler getRejectedExecutionHandler();
}
