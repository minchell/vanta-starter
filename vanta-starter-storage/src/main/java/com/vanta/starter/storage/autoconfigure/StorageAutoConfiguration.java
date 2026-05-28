package com.vanta.starter.storage.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.storage.core.LocalFileStorageTemplate;
import com.vanta.starter.storage.core.StorageTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Vanta 存储能力自动配置。
 *
 * <p>开启后默认注册本地文件存储模板。远程对象存储需要后续独立实现并替换 StorageTemplate。</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.STORAGE, name = PropertiesConstants.ENABLED, havingValue = "true")
public class StorageAutoConfiguration {

    /**
     * 注册默认本地文件存储模板。
     *
     * <p>该 Bean 只在业务方没有声明 {@link StorageTemplate} 且配置类型为 local 时创建。
     * 默认实现只写本地磁盘，不会连接 MinIO、S3、OSS 等远程对象存储。</p>
     *
     * @param properties 存储配置，用于读取本地根目录
     * @return 本地文件存储模板
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = PropertiesConstants.STORAGE, name = "type", havingValue = "local", matchIfMissing = true)
    public StorageTemplate storageTemplate(StorageProperties properties) {
        return new LocalFileStorageTemplate(properties.getLocal().getBasePath());
    }
}
