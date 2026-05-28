package com.vanta.starter.core.autoconfigure.threadpool;

import com.vanta.starter.core.constant.PropertiesConstants;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.task.ThreadPoolTaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 调度任务线程池配置。
 * <p>
 * 该配置在 {@code spring.task.scheduling.extension.enabled=true} 或未显式配置时生效，
 * 用于定制 Spring Boot 默认调度线程池的拒绝策略，并开启标准 {@link EnableScheduling} 能力。
 * </p>
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
@ConditionalOnProperty(prefix = "spring.task.scheduling.extension", name = PropertiesConstants.ENABLED, havingValue = "true")
class TaskSchedulingConfiguration {

    /**
     * 当前配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(TaskSchedulingConfiguration.class);

    /**
     * 创建调度任务线程池定制器。
     *
     * @param properties 线程池扩展配置属性。
     * @return Spring Boot 调度线程池定制器，用于设置拒绝策略。
     */
    @Bean
    public ThreadPoolTaskSchedulerCustomizer threadPoolTaskSchedulerCustomizer(ThreadPoolExtensionProperties properties) {
        return executor -> executor.setRejectedExecutionHandler(properties.getScheduling()
                .getRejectedPolicy()
                .getRejectedExecutionHandler());
    }

    /**
     * 输出调度任务线程池配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'TaskScheduler' completed initialization.");
    }
}
