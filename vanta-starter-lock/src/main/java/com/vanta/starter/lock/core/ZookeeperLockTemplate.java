package com.vanta.starter.lock.core;

import com.vanta.starter.lock.autoconfigure.LockProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Zookeeper Curator 的分布式锁模板。
 *
 * <p>该模板不创建 Zookeeper 连接，只复用业务项目注入的 CuratorFramework。
 * 它适合需要强一致协调语义、已有 Zookeeper 基础设施或 Spring Cloud Zookeeper 环境的系统。</p>
 */
public class ZookeeperLockTemplate implements DistributedLockTemplate {

    /**
     * Curator 客户端。
     *
     * <p>模板通过该客户端创建 InterProcessMutex，业务方可以替换 CuratorFramework Bean 接管连接策略。</p>
     */
    private final CuratorFramework curatorFramework;

    /**
     * Zookeeper 锁路径前缀。
     *
     * <p>前缀来自配置项 vanta-starter.lock.zookeeper-path-prefix，最终锁路径会落在该前缀下。</p>
     */
    private final String pathPrefix;

    /**
     * 创建 Zookeeper 分布式锁模板。
     *
     * @param curatorFramework Curator 客户端
     * @param properties       锁配置
     */
    public ZookeeperLockTemplate(CuratorFramework curatorFramework, LockProperties properties) {
        this.curatorFramework = Objects.requireNonNull(curatorFramework, "curatorFramework must not be null");
        Objects.requireNonNull(properties, "properties must not be null");
        this.pathPrefix = normalizePathPrefix(properties.getZookeeperPathPrefix());
    }

    /**
     * 使用 Zookeeper 分布式锁保护业务回调。
     *
     * @param lockKey  业务锁 key，会被归一化为 Zookeeper 安全路径片段
     * @param waitTime 等待获取锁的最长时间；超时未获取锁时返回 notAcquired
     * @param callback 获取锁后执行的业务逻辑
     * @param <T>      业务回调返回值类型
     * @return 锁执行结果，包含获取状态、业务返回值和异常信息
     */
    @Override
    public <T> LockExecutionResult<T> execute(String lockKey, Duration waitTime, Callable<T> callback) {
        Objects.requireNonNull(lockKey, "lockKey must not be null");
        Objects.requireNonNull(waitTime, "waitTime must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, buildLockPath(lockKey));
        boolean acquired = false;
        LockExecutionResult<T> result;
        try {
            acquired = mutex.acquire(toNonNegativeMillis(waitTime), TimeUnit.MILLISECONDS);
            if (!acquired) {
                return LockExecutionResult.notAcquired();
            }
            result = LockExecutionResult.acquired(callback.call());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            result = LockExecutionResult.failed(acquired, ex);
        } catch (Exception ex) {
            result = LockExecutionResult.failed(acquired, ex);
        }
        return releaseLock(mutex, acquired, result);
    }

    /**
     * 释放 Zookeeper 锁并把释放异常合并到执行结果中。
     *
     * @param mutex    Curator 分布式互斥锁
     * @param acquired 当前线程是否已获得锁
     * @param result   业务执行结果
     * @param <T>      业务回调返回值类型
     * @return 释放完成后的锁执行结果
     */
    private <T> LockExecutionResult<T> releaseLock(InterProcessMutex mutex, boolean acquired, LockExecutionResult<T> result) {
        if (!acquired) {
            return result;
        }
        try {
            mutex.release();
            return result;
        } catch (Exception ex) {
            if (result.error() != null) {
                result.error().addSuppressed(ex);
                return result;
            }
            return LockExecutionResult.failed(true, ex);
        }
    }

    /**
     * 根据业务锁 key 构造完整 Zookeeper 锁路径。
     *
     * @param lockKey 业务锁 key
     * @return 完整 Zookeeper 锁路径
     */
    private String buildLockPath(String lockKey) {
        if (lockKey.isBlank()) {
            throw new IllegalArgumentException("lockKey must not be blank");
        }
        return pathPrefix + "/key-" + encodePathSegment(lockKey);
    }

    /**
     * 将业务锁 key 编码为 Zookeeper 安全路径片段。
     *
     * <p>这里使用 URL-safe Base64，而不是简单替换非法字符，避免 order:1 和 order/1 这类 key 被归一化后发生碰撞。</p>
     *
     * @param lockKey 业务锁 key
     * @return 可作为 Zookeeper 节点名的路径片段
     */
    private String encodePathSegment(String lockKey) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(lockKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 归一化 Zookeeper 路径前缀。
     *
     * @param pathPrefix 配置中的路径前缀
     * @return 以单个斜杠开头且末尾不带斜杠的路径前缀
     */
    private String normalizePathPrefix(String pathPrefix) {
        if (pathPrefix == null || pathPrefix.isBlank()) {
            return "/vanta/lock";
        }
        String normalizedPrefix = pathPrefix.startsWith("/") ? pathPrefix : "/" + pathPrefix;
        while (normalizedPrefix.endsWith("/") && normalizedPrefix.length() > 1) {
            normalizedPrefix = normalizedPrefix.substring(0, normalizedPrefix.length() - 1);
        }
        return normalizedPrefix;
    }

    /**
     * 将等待时间转换为非负毫秒。
     *
     * @param duration 等待时间
     * @return 非负毫秒数
     */
    private long toNonNegativeMillis(Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("waitTime must not be negative");
        }
        return duration.toMillis();
    }
}
