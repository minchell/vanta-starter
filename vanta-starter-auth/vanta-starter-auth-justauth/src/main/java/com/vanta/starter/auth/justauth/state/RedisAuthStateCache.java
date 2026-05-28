package com.vanta.starter.auth.justauth.state;

import com.vanta.starter.auth.justauth.autoconfigure.JustAuthCacheProperties;
import com.vanta.starter.cache.redis.util.RedisUtils;
import me.zhyd.oauth.cache.AuthStateCache;

import java.time.Duration;

/**
 * Redis State 缓存实现
 */
public record RedisAuthStateCache(JustAuthCacheProperties cacheProperties) implements AuthStateCache {

    /**
     * 存入缓存
     *
     * @param key   key
     * @param value 内容
     */
    @Override
    public void cache(String key, String value) {
        this.cache(key, value, cacheProperties.getTimeout().toMillis());
    }

    /**
     * 存入缓存
     *
     * @param key     key
     * @param value   内容
     * @param timeout 缓存过期时间（毫秒）
     */
    @Override
    public void cache(String key, String value, long timeout) {
        RedisUtils.set(RedisUtils.formatKey(cacheProperties.getPrefix(), key), value, Duration.ofMillis(timeout));
    }

    /**
     * 获取缓存内容
     *
     * @param key key
     * @return 内容
     */
    @Override
    public String get(String key) {
        return RedisUtils.get(RedisUtils.formatKey(cacheProperties.getPrefix(), key));
    }

    /**
     * 是否存在 key，如果对应 key 的 value 值已过期，也返回 false
     *
     * @param key key
     * @return true：存在 key，并且 value 没过期；false：key 不存在或者已过期
     */
    @Override
    public boolean containsKey(String key) {
        return RedisUtils.exists(RedisUtils.formatKey(cacheProperties.getPrefix(), key));
    }
}