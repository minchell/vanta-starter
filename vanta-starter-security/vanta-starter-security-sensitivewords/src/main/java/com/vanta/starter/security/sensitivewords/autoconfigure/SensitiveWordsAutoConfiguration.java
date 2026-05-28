package com.vanta.starter.security.sensitivewords.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.security.sensitivewords.service.DefaultSensitiveWordsConfig;
import com.vanta.starter.security.sensitivewords.service.DefaultSensitiveWordsService;
import com.vanta.starter.security.sensitivewords.service.SensitiveWordsConfig;
import com.vanta.starter.security.sensitivewords.service.SensitiveWordsService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 敏感词自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(SensitiveWordsProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.SECURITY_SENSITIVE_WORDS, name = PropertiesConstants.ENABLED, havingValue = "true")
public class SensitiveWordsAutoConfiguration {

    /**
     * log 字段。
     * <p>用于保存 安全防护能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(SensitiveWordsAutoConfiguration.class);

    /**
     * 默认敏感词配置
     */
    @Bean
    @ConditionalOnMissingBean
    public SensitiveWordsConfig sensitiveWordsConfig(SensitiveWordsProperties properties) {
        return new DefaultSensitiveWordsConfig(properties);
    }

    /**
     * 默认敏感词服务
     */
    @Bean
    @ConditionalOnMissingBean
    public SensitiveWordsService sensitiveWordsService(SensitiveWordsConfig sensitiveWordsConfig) {
        return new DefaultSensitiveWordsService(sensitiveWordsConfig);
    }

    /**
     * 注册 void 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Security-Sensitive Words' completed initialization.");
    }
}
