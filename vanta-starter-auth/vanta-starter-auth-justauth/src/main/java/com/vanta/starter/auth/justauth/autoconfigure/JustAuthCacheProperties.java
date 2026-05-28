package com.vanta.starter.auth.justauth.autoconfigure;

import java.time.Duration;

/**
 * JustAuth 缓存配置属性
 *
 * @author <a href="https://gitee.com/justauth/justauth-spring-boot-starter">yangkai.shen</a>
 */
public class JustAuthCacheProperties {

    /**
     * 缓存类型
     */
    private CacheType type = CacheType.DEFAULT;

    /**
     * 缓存前缀
     */
    private String prefix = "JUSTAUTH::STATE::";

    /**
     * 超时时长
     * <p>
     * 目前仅 {@link #type CacheType.REDIS} 缓存生效（默认：3分钟）
     * </p>
     */
    private Duration timeout = Duration.ofMinutes(3);

    /**
     * 获取缓存类型。
     *
     * @return 缓存类型
     */
    public CacheType getType() {
        return type;
    }

    /**
     * 设置缓存类型。
     *
     * @param type 缓存类型
     */
    public void setType(CacheType type) {
        this.type = type;
    }

    /**
     * 获取缓存前缀。
     *
     * @return 缓存前缀
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置缓存前缀。
     *
     * @param prefix 缓存前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 获取超时时长。
     *
     * @return 超时时长
     */
    public Duration getTimeout() {
        return timeout;
    }

    /**
     * 设置超时时长。
     *
     * @param timeout 超时时长
     */
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    /**
     * 缓存类型枚举
     */
    public enum CacheType {

        /**
         * 使用 JustAuth 内置缓存
         */
        DEFAULT,

        /**
         * 使用 Redis 缓存
         */
        REDIS,

        /**
         * 自定义缓存
         */
        CUSTOM
    }
}
