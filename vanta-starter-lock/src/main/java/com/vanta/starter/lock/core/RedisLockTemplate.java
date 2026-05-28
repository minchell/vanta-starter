package com.vanta.starter.lock.core;

import com.vanta.starter.lock.autoconfigure.LockProperties;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis Redisson 的分布式锁模板。
 *
 * <p>该模板不创建 Redis 连接，只复用业务项目注入的 RedissonClient。
 * 它适合已经有 Redis 基础设施，并希望通过 Redisson 看门狗、Lua 原子操作和线程持有判断获得稳定锁语义的场景。</p>
 */
public class RedisLockTemplate implements DistributedLockTemplate {

    /**
     * Redisson 客户端。
     *
     * <p>starter 通过该客户端获取 RLock，业务方可以通过替换 RedissonClient Bean 管理连接、序列化和集群策略。</p>
     */
    private final RedissonClient redissonClient;

    /**
     * Redis 锁 key 前缀。
     *
     * <p>前缀来自配置项 vanta-starter.lock.redis-key-prefix，用于隔离不同系统和环境。</p>
     */
    private final String keyPrefix;

    /**
     * Redis 锁租约时间。
     *
     * <p>该时间会传给 Redisson tryLock 的 leaseTime 参数，避免业务进程异常退出后锁永久残留。</p>
     */
    private final Duration leaseTime;

    /**
     * 创建 Redis 分布式锁模板。
     *
     * @param redissonClient Redisson 客户端
     * @param properties     锁配置
     */
    public RedisLockTemplate(RedissonClient redissonClient, LockProperties properties) {
        this.redissonClient = Objects.requireNonNull(redissonClient, "redissonClient must not be null");
        Objects.requireNonNull(properties, "properties must not be null");
        this.keyPrefix = normalizePrefix(properties.getRedisKeyPrefix());
        this.leaseTime = normalizePositiveDuration(properties.getLeaseTime(), "leaseTime");
    }

    /**
     * 使用 Redis 分布式锁保护业务回调。
     *
     * @param lockKey  业务锁 key，最终 Redis key 为 redisKeyPrefix + lockKey
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
        RLock lock = redissonClient.getLock(keyPrefix + lockKey);
        boolean acquired = false;
        LockExecutionResult<T> result;
        try {
            acquired = lock.tryLock(toNonNegativeMillis(waitTime), leaseTime.toMillis(), TimeUnit.MILLISECONDS);
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
        return releaseLock(lock, acquired, result);
    }

    /**
     * 释放 Redis 锁并把释放异常合并到执行结果中。
     *
     * @param lock     Redisson 锁对象
     * @param acquired 当前线程是否已获得锁
     * @param result   业务执行结果
     * @param <T>      业务回调返回值类型
     * @return 释放完成后的锁执行结果
     */
    private <T> LockExecutionResult<T> releaseLock(RLock lock, boolean acquired, LockExecutionResult<T> result) {
        if (!acquired) {
            return result;
        }
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
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
     * 归一化 Redis key 前缀。
     *
     * @param keyPrefix 配置中的 key 前缀
     * @return 非 null 的 key 前缀
     */
    private String normalizePrefix(String keyPrefix) {
        return keyPrefix == null ? "" : keyPrefix;
    }

    /**
     * 校验并归一化必须大于 0 的时间配置。
     *
     * @param duration 配置时间
     * @param name     配置名称
     * @return 已校验的时间
     */
    private Duration normalizePositiveDuration(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " must not be null");
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
        return duration;
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
