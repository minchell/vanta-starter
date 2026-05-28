package com.vanta.starter.core.autoconfigure.application;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 应用基础能力自动配置。
 * <p>
 * 该配置负责把 {@link ApplicationProperties} 注册到 Spring 容器，并导入 Hutool 的 Spring 工具支持。
 * 它只做本地 Bean 注册，不会主动访问数据库、缓存、消息队列或任何远程服务。
 * </p>
 */
@AutoConfiguration
@ComponentScan("cn.hutool.extra.spring")
@Import(cn.hutool.extra.spring.SpringUtil.class)
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationAutoConfiguration {
}
