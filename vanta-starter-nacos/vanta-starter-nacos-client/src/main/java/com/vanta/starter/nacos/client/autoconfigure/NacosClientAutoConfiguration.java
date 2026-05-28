package com.vanta.starter.nacos.client.autoconfigure;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.nacos.client.core.VantaNacosConfigTemplate;
import com.vanta.starter.nacos.client.core.VantaNacosNamingTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * Nacos 原生客户端自动配置。
 *
 * <p>该自动配置不替代 Spring Cloud Alibaba 的配置中心/注册发现能力，
 * 只是在业务需要直接读写配置或查询实例时提供轻量模板。</p>
 */
@AutoConfiguration
@ConditionalOnClass(ConfigService.class)
@EnableConfigurationProperties(NacosClientProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.NACOS_CLIENT, name = PropertiesConstants.ENABLED, havingValue = "true")
public class NacosClientAutoConfiguration {

    /**
     * 注册 Nacos 配置中心原生客户端。
     *
     * @param properties Nacos 原生客户端配置
     * @return Nacos ConfigService
     * @throws NacosException 创建 Nacos 客户端失败时抛出
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigService nacosConfigService(NacosClientProperties properties) throws NacosException {
        return NacosFactory.createConfigService(toNacosProperties(properties));
    }

    /**
     * 注册 Nacos 服务发现原生客户端。
     *
     * @param properties Nacos 原生客户端配置
     * @return Nacos NamingService
     * @throws NacosException 创建 Nacos 客户端失败时抛出
     */
    @Bean
    @ConditionalOnMissingBean
    public NamingService nacosNamingService(NacosClientProperties properties) throws NacosException {
        return NacosFactory.createNamingService(toNacosProperties(properties));
    }

    /**
     * 注册 Vanta Nacos 配置模板。
     *
     * @param configService Nacos ConfigService
     * @return Vanta Nacos 配置模板
     */
    @Bean
    @ConditionalOnMissingBean
    public VantaNacosConfigTemplate vantaNacosConfigTemplate(ConfigService configService) {
        return new VantaNacosConfigTemplate(configService);
    }

    /**
     * 注册 Vanta Nacos 服务发现模板。
     *
     * @param namingService Nacos NamingService
     * @return Vanta Nacos 服务发现模板
     */
    @Bean
    @ConditionalOnMissingBean
    public VantaNacosNamingTemplate vantaNacosNamingTemplate(NamingService namingService) {
        return new VantaNacosNamingTemplate(namingService);
    }

    /**
     * 将 Vanta 配置转换为 Nacos 原生 Properties。
     *
     * @param properties Nacos 原生客户端配置
     * @return Nacos SDK 识别的配置集合
     */
    private Properties toNacosProperties(NacosClientProperties properties) {
        Properties nacosProperties = new Properties();
        nacosProperties.setProperty(PropertyKeyConst.SERVER_ADDR, properties.getServerAddr());
        if (StringUtils.hasText(properties.getNamespace())) {
            nacosProperties.setProperty(PropertyKeyConst.NAMESPACE, properties.getNamespace());
        }
        if (StringUtils.hasText(properties.getUsername())) {
            nacosProperties.setProperty(PropertyKeyConst.USERNAME, properties.getUsername());
        }
        if (StringUtils.hasText(properties.getPassword())) {
            nacosProperties.setProperty(PropertyKeyConst.PASSWORD, properties.getPassword());
        }
        return nacosProperties;
    }
}
