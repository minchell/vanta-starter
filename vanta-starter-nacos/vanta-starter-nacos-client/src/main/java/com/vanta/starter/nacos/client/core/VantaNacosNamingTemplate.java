package com.vanta.starter.nacos.client.core;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * Vanta Nacos 服务发现模板。
 *
 * <p>该模板封装 NamingService 的常用实例操作，适合业务在运行时主动查询、
 * 注册或注销服务实例。</p>
 */
public class VantaNacosNamingTemplate {

    /**
     * Nacos 服务发现原生客户端。
     * <p>所有实例查询、注册和注销操作都通过该客户端完成。</p>
     */
    private final NamingService namingService;

    /**
     * 创建 Vanta Nacos 服务发现模板。
     *
     * @param namingService Nacos NamingService
     */
    public VantaNacosNamingTemplate(NamingService namingService) {
        this.namingService = namingService;
    }

    /**
     * 读取底层 Nacos NamingService。
     *
     * @return Nacos NamingService
     */
    public NamingService getNamingService() {
        return namingService;
    }

    /**
     * 查询服务实例列表。
     *
     * @param serviceName 服务名
     * @param groupName   服务分组
     * @return 实例列表
     */
    public List<Instance> getAllInstances(String serviceName, String groupName) throws NacosException {
        return namingService.getAllInstances(serviceName, groupName);
    }

    /**
     * 注册服务实例。
     *
     * @param serviceName 服务名
     * @param groupName   服务分组
     * @param ip          实例 IP
     * @param port        实例端口
     */
    public void registerInstance(String serviceName, String groupName, String ip, int port) throws NacosException {
        namingService.registerInstance(serviceName, groupName, ip, port);
    }

    /**
     * 注销服务实例。
     *
     * @param serviceName 服务名
     * @param groupName   服务分组
     * @param ip          实例 IP
     * @param port        实例端口
     */
    public void deregisterInstance(String serviceName, String groupName, String ip, int port) throws NacosException {
        namingService.deregisterInstance(serviceName, groupName, ip, port);
    }
}
