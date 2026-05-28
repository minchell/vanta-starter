package com.vanta.starter.nacos.client.core;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

/**
 * Vanta Nacos 配置模板。
 *
 * <p>该模板封装 Nacos ConfigService 的常用读写操作，避免业务代码直接散落
 * dataId、group、timeout 等调用细节。</p>
 */
public class VantaNacosConfigTemplate {

    /**
     * Nacos 配置中心原生客户端。
     * <p>所有配置读取、发布、删除和监听操作都通过该客户端完成。</p>
     */
    private final ConfigService configService;

    /**
     * 创建 Vanta Nacos 配置模板。
     *
     * @param configService Nacos ConfigService
     */
    public VantaNacosConfigTemplate(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * 读取底层 Nacos ConfigService。
     *
     * @return Nacos ConfigService
     */
    public ConfigService getConfigService() {
        return configService;
    }

    /**
     * 读取配置内容。
     *
     * @param dataId    配置 dataId
     * @param group     配置分组
     * @param timeoutMs 请求超时时间，单位毫秒
     * @return 配置内容
     */
    public String getConfig(String dataId, String group, long timeoutMs) throws NacosException {
        return configService.getConfig(dataId, group, timeoutMs);
    }

    /**
     * 发布配置。
     *
     * @param dataId  配置 dataId
     * @param group   配置分组
     * @param content 配置内容
     * @return true 表示发布成功
     */
    public boolean publishConfig(String dataId, String group, String content) throws NacosException {
        return configService.publishConfig(dataId, group, content);
    }

    /**
     * 删除配置。
     *
     * @param dataId 配置 dataId
     * @param group  配置分组
     * @return true 表示删除成功
     */
    public boolean removeConfig(String dataId, String group) throws NacosException {
        return configService.removeConfig(dataId, group);
    }

    /**
     * 添加配置监听器。
     *
     * @param dataId   配置 dataId
     * @param group    配置分组
     * @param listener Nacos 原生监听器
     */
    public void addListener(String dataId, String group, Listener listener) throws NacosException {
        configService.addListener(dataId, group, listener);
    }
}
