package com.vanta.starter.encrypt.password.encoder.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.util.validation.CheckUtils;
import com.vanta.starter.encrypt.password.encoder.enums.PasswordEncoderAlgorithm;
import com.vanta.starter.encrypt.password.encoder.util.PasswordEncoderUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码编码器自动配置
 */
@AutoConfiguration
@EnableConfigurationProperties(PasswordEncoderProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.ENCRYPT_PASSWORD_ENCODER, name = PropertiesConstants.ENABLED, havingValue = "true")
public class PasswordEncoderAutoConfiguration {

    /**
     * log 字段。
     * <p>用于保存 加密能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(PasswordEncoderAutoConfiguration.class);

    /**
     * 密码编码器配置
     *
     * @see DelegatingPasswordEncoder
     * @see PasswordEncoderFactories
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder(PasswordEncoderProperties properties) {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(PasswordEncoderAlgorithm.BCRYPT.name().toLowerCase(), PasswordEncoderUtil.getEncoder(PasswordEncoderAlgorithm.BCRYPT));
        encoders.put(PasswordEncoderAlgorithm.SCRYPT.name().toLowerCase(), PasswordEncoderUtil.getEncoder(PasswordEncoderAlgorithm.SCRYPT));
        encoders.put(PasswordEncoderAlgorithm.PBKDF2.name().toLowerCase(), PasswordEncoderUtil.getEncoder(PasswordEncoderAlgorithm.PBKDF2));
        encoders.put(PasswordEncoderAlgorithm.ARGON2.name().toLowerCase(), PasswordEncoderUtil.getEncoder(PasswordEncoderAlgorithm.ARGON2));
        PasswordEncoderAlgorithm algorithm = properties.getAlgorithm();
        CheckUtils.throwIf(PasswordEncoderUtil.getEncoder(algorithm) == null, "不支持的加密算法: {}", algorithm);
        return new DelegatingPasswordEncoder(algorithm.name().toLowerCase(), encoders);
    }

    /**
     * 执行 postConstruct 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Encrypt-Password Encoder' completed initialization.");
    }
}
