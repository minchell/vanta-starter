package com.vanta.starter.auth.justauth.autoconfigure;

import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.request.AuthRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * JustAuth 扩展配置属性
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 */
public class JustAuthExtendProperties {

    /**
     * 枚举类全路径
     * <p>enumClass 参数，调用方应传入与 认证授权能力 场景匹配的有效值</p>
     */
    private Class<? extends AuthSource> enumClass;

    /**
     * 扩展请求配置
     * <p>config 参数，调用方应传入与 认证授权能力 场景匹配的有效值</p>
     */
    private Map<String, ExtendRequestConfig> config = new HashMap<>();

    /**
     * 获取枚举类全路径。
     *
     * @return 枚举类全路径
     */
    public Class<? extends AuthSource> getEnumClass() {
        return enumClass;
    }

    /**
     * 设置枚举类全路径。
     *
     * @param enumClass 枚举类全路径
     */
    public void setEnumClass(Class<? extends AuthSource> enumClass) {
        this.enumClass = enumClass;
    }

    /**
     * 获取扩展请求配置。
     *
     * @return 扩展请求配置
     */
    public Map<String, ExtendRequestConfig> getConfig() {
        return config;
    }

    /**
     * 设置扩展请求配置。
     *
     * @param config 扩展请求配置
     */
    public void setConfig(Map<String, ExtendRequestConfig> config) {
        this.config = config;
    }

    /**
     * 扩展请求配置
     */
    public static class ExtendRequestConfig extends AuthConfig {

        /**
         * 平台对应的 AuthRequest 实现类
         */
        private Class<? extends AuthRequest> requestClass;

        /**
         * 读取 Request Class 配置或状态。
         * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
         *
         * @return 方法执行后的结果对象、配置值或运行时依赖
         */
        public Class<? extends AuthRequest> getRequestClass() {
            return requestClass;
        }

        /**
         * 设置 Request Class 配置值。
         * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
         *
         * @param requestClass requestClass 参数，调用方应传入与 认证授权能力 场景匹配的有效值
         */
        public void setRequestClass(Class<? extends AuthRequest> requestClass) {
            this.requestClass = requestClass;
        }
    }
}
