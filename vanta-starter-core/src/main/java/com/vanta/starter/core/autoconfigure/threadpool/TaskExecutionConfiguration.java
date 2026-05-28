package com.vanta.starter.core.autoconfigure.threadpool;

import cn.hutool.core.util.ArrayUtil;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.exception.BaseException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * 异步任务线程池配置。
 * <p>
 * 该配置在 {@code spring.task.execution.extension.enabled=true} 或未显式配置时生效，
 * 用于定制 Spring Boot 默认的 {@link ThreadPoolTaskExecutor}，并为 {@link Async} 异步方法提供异常处理器。
 * </p>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.task.execution.extension", name = PropertiesConstants.ENABLED, havingValue = "true")
class TaskExecutionConfiguration {

    /**
     * 当前配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(TaskExecutionConfiguration.class);

    /**
     * 核心线程数。
     * <p>
     * 默认值为可用处理器数量加一，适合作为通用 Web 应用的保守起点。
     * </p>
     */
    @Value("${spring.task.execution.pool.core-size:#{T(java.lang.Runtime).getRuntime().availableProcessors() + 1}}")
    private int corePoolSize;

    /**
     * 最大线程数。
     * <p>
     * 默认值为可用处理器数量的两倍，业务方可以按任务类型和吞吐需求通过配置覆盖。
     * </p>
     */
    @Value("${spring.task.execution.pool.max-size:#{T(java.lang.Runtime).getRuntime().availableProcessors() * 2}}")
    private int maxPoolSize;

    /**
     * 创建异步任务线程池定制器。
     *
     * @param properties 线程池扩展配置属性。
     * @return Spring Boot 线程池定制器，用于设置核心线程数、最大线程数和拒绝策略。
     */
    @Bean
    public ThreadPoolTaskExecutorCustomizer threadPoolTaskExecutorCustomizer(ThreadPoolExtensionProperties properties) {
        return executor -> {
            // 核心（最小）线程数
            executor.setCorePoolSize(corePoolSize);
            // 最大线程数
            executor.setMaxPoolSize(maxPoolSize);
            // 当线程池的任务缓存队列已满并且线程池中的线程数已达到 maxPoolSize 时采取的任务拒绝策略
            executor.setRejectedExecutionHandler(properties.getExecution()
                    .getRejectedPolicy()
                    .getRejectedExecutionHandler());
        };
    }

    /**
     * 输出异步任务线程池配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'TaskExecutor' completed initialization.");
    }

    /**
     * {@link Async} 异步任务线程池配置
     */
    @EnableAsync(proxyTargetClass = true)
    static class AsyncThreadPoolConfigurer implements AsyncConfigurer {

        /**
         * Spring Boot 默认异步任务线程池。
         */
        private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

        /**
         * 创建异步线程池配置器。
         *
         * @param threadPoolTaskExecutor Spring Boot 默认异步任务线程池。
         */
        public AsyncThreadPoolConfigurer(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
            this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        }

        /**
         * 获取 {@link Async} 使用的执行器。
         *
         * @return 异步任务执行器。
         */
        @Override
        public Executor getAsyncExecutor() {
            return threadPoolTaskExecutor;
        }

        /**
         * 创建异步任务未捕获异常处理器。
         * <p>
         * 当 {@link Async} 方法返回 {@code void} 且执行过程抛出异常时，Spring 会调用该处理器。
         * 这里把异常消息、方法名和参数值合并后抛出 {@link BaseException}，便于上层日志统一记录。
         * </p>
         *
         * @return 异步任务未捕获异常处理器。
         */
        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return (throwable, method, objects) -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Exception message: ")
                        .append(throwable.getMessage())
                        .append(", Method name: ")
                        .append(method.getName());
                if (ArrayUtil.isNotEmpty(objects)) {
                    sb.append(", Parameter value: ").append(Arrays.toString(objects));
                }
                throw new BaseException(sb.toString());
            };
        }

        /**
         * 输出 {@link Async} 线程池配置初始化完成日志。
         */
        @PostConstruct
        public void postConstruct() {
            log.debug("[Vanta Starter] - Auto Configuration 'TaskExecutor-@Async' completed initialization.");
        }
    }
}
