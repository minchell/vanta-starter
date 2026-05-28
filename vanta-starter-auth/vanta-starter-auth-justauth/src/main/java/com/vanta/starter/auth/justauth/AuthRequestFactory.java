package com.vanta.starter.auth.justauth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.vanta.starter.auth.justauth.autoconfigure.JustAuthExtendProperties;
import com.vanta.starter.auth.justauth.autoconfigure.JustAuthHttpProperties;
import com.vanta.starter.auth.justauth.autoconfigure.JustAuthProperties;
import com.xkcoding.http.config.HttpConfig;
import me.zhyd.oauth.AuthRequestBuilder;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.request.AuthRequest;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthRequest 工厂类
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 */
public class AuthRequestFactory {

    /**
     * properties 字段。
     * <p>用于保存 认证授权能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final JustAuthProperties properties;
    /**
     * stateCache 字段。
     * <p>用于保存 认证授权能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final AuthStateCache stateCache;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param properties properties 参数，调用方应传入与 认证授权能力 场景匹配的有效值
     * @param stateCache stateCache 参数，调用方应传入与 认证授权能力 场景匹配的有效值
     */
    public AuthRequestFactory(JustAuthProperties properties, AuthStateCache stateCache) {
        this.properties = properties;
        this.stateCache = stateCache;
    }

    /**
     * 获取 AuthRequest
     *
     * @param source {@link AuthSource}
     * @return {@link AuthRequest}
     */
    public AuthRequest getAuthRequest(String source) {
        if (StrUtil.isBlank(source)) {
            throw new AuthException(AuthResponseStatus.NO_AUTH_SOURCE);
        }

        // 获取内置 AuthRequest
        AuthRequest authRequest = this.getDefaultAuthRequest(source);

        // 获取自定义 AuthRequest
        if (authRequest == null) {
            authRequest = this.getExtendAuthRequest(properties.getExtend().getEnumClass(), source);
        }

        if (authRequest == null) {
            throw new AuthException(AuthResponseStatus.UNSUPPORTED);
        }
        return authRequest;
    }

    /**
     * 获取自定义 AuthRequest
     *
     * @param clazz  枚举类 {@link AuthSource}
     * @param source {@link AuthSource}
     * @return {@link AuthRequest}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private AuthRequest getExtendAuthRequest(Class clazz, String source) {
        String upperSource = source.toUpperCase();
        try {
            EnumUtil.fromString(clazz, upperSource);
        } catch (IllegalArgumentException e) {
            // 无自定义匹配
            return null;
        }

        Map<String, JustAuthExtendProperties.ExtendRequestConfig> extendConfig = properties.getExtend().getConfig();
        Map<String, JustAuthExtendProperties.ExtendRequestConfig> upperConfig = new HashMap<>(6);
        extendConfig.forEach((k, v) -> upperConfig.put(k.toUpperCase(), v));
        JustAuthExtendProperties.ExtendRequestConfig extendRequestConfig = upperConfig.get(upperSource);
        if (extendRequestConfig != null) {
            // 配置 HTTP
            this.configureHttpConfig(upperSource, extendRequestConfig, properties.getHttp());

            Class<? extends AuthRequest> requestClass = extendRequestConfig.getRequestClass();

            if (requestClass != null) {
                // 反射获取 Request 对象，所以必须实现 2 个参数的构造方法
                return ReflectUtil.newInstance(requestClass, extendRequestConfig, stateCache);
            }
        }
        return null;
    }

    /**
     * 获取内置 AuthRequest
     *
     * @param source {@link AuthSource}
     * @return {@link AuthRequest}
     */
    private AuthRequest getDefaultAuthRequest(String source) {
        AuthDefaultSource authSource;
        try {
            authSource = EnumUtil.fromString(AuthDefaultSource.class, source.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 无自定义匹配
            return null;
        }

        AuthConfig config = properties.getType().get(authSource.name());
        // 找不到对应关系，直接返回空
        if (config == null) {
            return null;
        }

        // 配置 HTTP
        this.configureHttpConfig(authSource.name(), config, properties.getHttp());
        return AuthRequestBuilder.builder().source(source).authConfig(config).build();
    }

    /**
     * 配置 HTTP 相关的配置
     *
     * @param authSource {@link AuthSource}
     * @param authConfig {@link AuthConfig}
     * @param httpConfig {@link JustAuthHttpProperties}
     */
    private void configureHttpConfig(String authSource, AuthConfig authConfig, JustAuthHttpProperties httpConfig) {
        if (null == httpConfig) {
            return;
        }

        Map<String, JustAuthHttpProperties.JustAuthProxyConfig> proxyConfigMap = httpConfig.getProxy();
        if (CollUtil.isEmpty(proxyConfigMap)) {
            return;
        }

        JustAuthHttpProperties.JustAuthProxyConfig proxyConfig = proxyConfigMap.get(authSource);
        if (null == proxyConfig) {
            return;
        }

        authConfig.setHttpConfig(HttpConfig.builder()
                .timeout(httpConfig.getTimeout())
                .proxy(new Proxy(Proxy.Type.valueOf(proxyConfig.getType()), new InetSocketAddress(proxyConfig.getHostname(), proxyConfig.getPort())))
                .build());
    }
}
