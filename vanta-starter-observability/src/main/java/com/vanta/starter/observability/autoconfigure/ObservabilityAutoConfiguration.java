package com.vanta.starter.observability.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.observability.core.ObservationContextHolder;
import com.vanta.starter.observability.core.ObservationFieldContributor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Vanta 观测能力自动配置。
 *
 * <p>开启后只注册本地上下文持有器和默认字段贡献器，不产生远程副作用。</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(ObservabilityProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.OBSERVABILITY, name = PropertiesConstants.ENABLED, havingValue = "true")
public class ObservabilityAutoConfiguration {

    /**
     * 注册默认观测上下文持有器。
     *
     * <p>业务方没有自定义 {@link ObservationContextHolder} 时使用该默认实现；
     * 它只在本地线程上下文中保存数据，不会写入远程日志、指标或链路平台。</p>
     *
     * @return 默认观测上下文持有器
     */
    @Bean
    @ConditionalOnMissingBean
    public ObservationContextHolder observationContextHolder() {
        return new ObservationContextHolder();
    }

    /**
     * 注册默认观测字段贡献器。
     *
     * <p>默认实现直接返回上下文中的扩展字段，业务方可以声明同类型 Bean
     * 来改造成日志字段、指标标签或链路属性。</p>
     *
     * @return 默认观测字段贡献器
     */
    @Bean
    @ConditionalOnMissingBean
    public ObservationFieldContributor observationFieldContributor() {
        return context -> context.fields();
    }
}
