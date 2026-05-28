package com.vanta.starter.lock.core;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JVM 内本地锁实现。
 *
 * <p>该实现不依赖外部中间件，适合单元测试、单进程任务保护和 starter 默认实现。
 * 它不是跨进程分布式锁，生产集群场景应切换为 Redis、Zookeeper 或 JDBC 实现。</p>
 */
public class LocalJvmLockTemplate implements DistributedLockTemplate {

    /**
     * JVM 内锁缓存。
     *
     * <p>key 是业务锁名称，value 是该 key 对应的 ReentrantLock。这里不主动删除锁对象，
     * 因为 local 实现定位是轻量默认实现；如果业务锁 key 数量无限增长，应改用远程锁实现或自定义清理策略。</p>
     */
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    /**
     * 使用 JVM 内锁保护业务回调。
     *
     * @param lockKey  锁 key，同一个 JVM 内相同 key 会互斥执行
     * @param waitTime 等待获取锁的最长时间；超过该时间未拿到锁时返回 notAcquired
     * @param callback 获取锁后执行的业务逻辑
     * @param <T>      业务回调返回值类型
     * @return 锁执行结果，包含是否拿到锁、回调返回值和异常信息
     */
    @Override
    public <T> LockExecutionResult<T> execute(String lockKey, Duration waitTime, Callable<T> callback) {
        Objects.requireNonNull(lockKey, "lockKey must not be null");
        Objects.requireNonNull(waitTime, "waitTime must not be null");
        Objects.requireNonNull(callback, "callback must not be null");
        ReentrantLock lock = locks.computeIfAbsent(lockKey, ignored -> new ReentrantLock());
        boolean acquired = false;
        try {
            acquired = lock.tryLock(toNonNegativeMillis(waitTime), TimeUnit.MILLISECONDS);
            if (!acquired) {
                return LockExecutionResult.notAcquired();
            }
            return LockExecutionResult.acquired(callback.call());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return LockExecutionResult.failed(acquired, ex);
        } catch (Exception ex) {
            return LockExecutionResult.failed(acquired, ex);
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
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
