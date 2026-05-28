package com.vanta.starter.encrypt.field.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.encrypt.field.interceptor.MyBatisDecryptInterceptor;
import com.vanta.starter.encrypt.field.interceptor.MyBatisEncryptInterceptor;
import com.vanta.starter.encrypt.field.util.EncryptHelper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 字段加密自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(FieldEncryptProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.ENCRYPT_FIELD, name = PropertiesConstants.ENABLED, havingValue = "true")
public class FieldEncryptAutoConfiguration {

    /**
     * log 字段。
     * <p>用于保存 加密能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(FieldEncryptAutoConfiguration.class);
    /**
     * properties 字段。
     * <p>用于保存 加密能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final FieldEncryptProperties properties;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param properties properties 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public FieldEncryptAutoConfiguration(FieldEncryptProperties properties) {
        this.properties = properties;
    }

    /**
     * MyBatis 加密拦截器配置
     */
    @Bean
    @ConditionalOnMissingBean
    public MyBatisEncryptInterceptor mybatisEncryptInterceptor() {
        return new MyBatisEncryptInterceptor();
    }

    /**
     * MyBatis 解密拦截器配置
     */
    @Bean
    @ConditionalOnMissingBean(MyBatisDecryptInterceptor.class)
    public MyBatisDecryptInterceptor mybatisDecryptInterceptor() {
        return new MyBatisDecryptInterceptor();
    }

    /**
     * 注册 void 默认 Bean。
     * 该 Bean 仅在配置条件满足且业务方未提供同类型 Bean 时创建，确保 starter 默认能力可以被替换。
     */
    @PostConstruct
    public void postConstruct() {
        EncryptHelper.init(properties);
        log.debug("[Vanta Starter] - Auto Configuration 'Encrypt-Field' completed initialization.");
    }
}
