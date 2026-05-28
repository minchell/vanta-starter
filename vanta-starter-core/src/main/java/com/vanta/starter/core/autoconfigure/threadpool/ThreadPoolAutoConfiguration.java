package com.vanta.starter.core.autoconfigure.threadpool;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

/**
 * 线程池扩展自动配置入口。
 * <p>
 * 该配置聚合异步任务线程池和调度任务线程池的增强配置，主要补齐核心线程数、最大线程数和拒绝策略等默认行为。
 * 该配置只调整 Spring 本地线程池，不会创建远程连接；业务方可以通过关闭对应配置开关或自定义 Bean 覆盖默认行为。
 * </p>
 */
@Lazy
@AutoConfiguration
@EnableConfigurationProperties(ThreadPoolExtensionProperties.class)
@Import({TaskExecutionConfiguration.class, TaskSchedulingConfiguration.class})
public class ThreadPoolAutoConfiguration {
}
