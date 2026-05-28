package com.vanta.starter.scheduler.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.scheduler.core.LocalScheduledTaskExecutor;
import com.vanta.starter.scheduler.core.ScheduledTaskExecutor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Vanta 调度能力自动配置。
 *
 * <p>开启后只注册任务执行器，不会主动创建任何定时任务。</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(SchedulerProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.SCHEDULER, name = PropertiesConstants.ENABLED, havingValue = "true")
public class SchedulerAutoConfiguration {

    /**
     * 注册默认本地同步任务执行器。
     *
     * <p>该 Bean 只在业务方没有声明 {@link ScheduledTaskExecutor} 且配置类型为 local 时创建。
     * 默认实现不会主动调度任务，只提供一个可被业务代码调用的执行抽象。</p>
     *
     * @return 本地同步任务执行器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = PropertiesConstants.SCHEDULER, name = "type", havingValue = "local", matchIfMissing = true)
    public ScheduledTaskExecutor scheduledTaskExecutor() {
        return new LocalScheduledTaskExecutor();
    }
}
