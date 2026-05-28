package com.vanta.starter.auth.justauth.autoconfigure;

import java.net.Proxy;
import java.util.Map;

/**
 * JustAuth HTTP 配置属性
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 */
public class JustAuthHttpProperties {

    /**
     * 超时时间（单位：毫秒）
     * <p>timeout 参数，调用方应传入与 认证授权能力 场景匹配的有效值</p>
     */
    private int timeout;

    /**
     * 代理配置
     * <p>proxy 参数，调用方应传入与 认证授权能力 场景匹配的有效值</p>
     */
    private Map<String, JustAuthProxyConfig> proxy;

    /**
     * 获取超时时间（单位：毫秒）。
     *
     * @return 超时时间（单位：毫秒）
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置超时时间（单位：毫秒）。
     *
     * @param timeout 超时时间（单位：毫秒）
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 获取代理配置。
     *
     * @return 代理配置
     */
    public Map<String, JustAuthProxyConfig> getProxy() {
        return proxy;
    }

    /**
     * 设置代理配置。
     *
     * @param proxy 代理配置
     */
    public void setProxy(Map<String, JustAuthProxyConfig> proxy) {
        this.proxy = proxy;
    }

    /**
     * 代理配置
     */
    public static class JustAuthProxyConfig {

        /**
         * 代理类型
         */
        private String type = Proxy.Type.HTTP.name();

        /**
         * 代理主机名
         */
        private String hostname;

        /**
         * 代理端口号
         */
        private int port;

        /**
         * 获取代理类型。
         *
         * @return 代理类型
         */
        public String getType() {
            return type;
        }

        /**
         * 设置代理类型。
         *
         * @param type 代理类型
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * 获取代理主机名。
         *
         * @return 代理主机名
         */
        public String getHostname() {
            return hostname;
        }

        /**
         * 设置代理主机名。
         *
         * @param hostname 代理主机名
         */
        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        /**
         * 获取代理端口号。
         *
         * @return 代理端口号
         */
        public int getPort() {
            return port;
        }

        /**
         * 设置代理端口号。
         *
         * @param port 代理端口号
         */
        public void setPort(int port) {
            this.port = port;
        }
    }
}
