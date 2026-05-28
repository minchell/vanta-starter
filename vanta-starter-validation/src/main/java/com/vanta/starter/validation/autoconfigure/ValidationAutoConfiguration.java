package com.vanta.starter.validation.autoconfigure;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Validator;
import org.hibernate.validator.BaseHibernateValidatorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Vanta Validation 自动配置。
 * <p>
 * 该配置注册 Jakarta Bean Validation 使用的 {@link Validator}，并接入 Spring {@link MessageSource} 支持国际化校验消息。
 * 默认开启 Hibernate Validator 快速失败模式，校验到第一个失败约束后立即返回，减少无意义的后续校验成本。
 * </p>
 */
@AutoConfiguration
@AutoConfigureBefore
public class ValidationAutoConfiguration {

    /**
     * 当前自动配置类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(ValidationAutoConfiguration.class);

    /**
     * Validator 失败立即返回模式配置
     *
     * <p>
     * 默认情况下会校验完所有字段，然后才抛出异常；快速失败模式会在第一个失败约束处停止。
     * </p>
     *
     * @param messageSource Spring 国际化消息源。
     * @return Jakarta Bean Validation 校验器。
     */
    @Bean
    public Validator validator(MessageSource messageSource) {
        try (LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean()) {
            // 国际化
            factoryBean.setValidationMessageSource(messageSource);
            // 快速失败
            factoryBean.getValidationPropertyMap().put(BaseHibernateValidatorConfiguration.FAIL_FAST, Boolean.TRUE.toString());
            factoryBean.afterPropertiesSet();
            return factoryBean.getValidator();
        }
    }

    /**
     * 输出 Validation 自动配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Validation' completed initialization.");
    }
}
