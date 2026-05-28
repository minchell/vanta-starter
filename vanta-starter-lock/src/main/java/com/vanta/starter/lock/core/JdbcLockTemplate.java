package com.vanta.starter.lock.core;

import com.vanta.starter.lock.autoconfigure.LockProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * 基于关系型数据库的 JDBC 分布式锁模板。
 *
 * <p>该模板不创建 DataSource，也不自动建表，只复用业务项目注入的 JdbcOperations。
 * 抢锁逻辑通过主键插入和过期行接管完成，适合没有 Redis/Zookeeper 但已经具备数据库基础设施的系统。</p>
 */
public class JdbcLockTemplate implements DistributedLockTemplate {

    /**
     * Spring JDBC 操作入口。
     *
     * <p>模板通过该对象执行 INSERT、UPDATE、DELETE，业务方可以替换 JdbcOperations Bean 接管数据源与事务策略。</p>
     */
    private final JdbcOperations jdbcOperations;

    /**
     * JDBC 锁表名称。
     *
     * <p>该值来自配置项 vanta-starter.lock.jdbc-table-name，并在构造阶段通过白名单校验。</p>
     */
    private final String tableName;

    /**
     * JDBC 锁租约时间。
     *
     * <p>抢锁成功后 expire_at = now + leaseTime，业务进程异常退出时其他节点可在过期后接管。</p>
     */
    private final Duration leaseTime;

    /**
     * JDBC 抢锁重试间隔。
     *
     * <p>waitTime 大于 0 且首次抢锁失败时，模板会按该间隔继续尝试，直到成功或超时。</p>
     */
    private final Duration retryInterval;

    /**
     * 创建 JDBC 分布式锁模板。
     *
     * @param jdbcOperations Spring JDBC 操作入口
     * @param properties     锁配置
     */
    public JdbcLockTemplate(JdbcOperations jdbcOperations, LockProperties properties) {
        this.jdbcOperations = Objects.requireNonNull(jdbcOperations, "jdbcOperations must not be null");
        Objects.requireNonNull(properties, "properties must not be null");
        this.tableName = validateTableName(properties.getJdbcTableName());
        this.leaseTime = normalizePositiveDuration(properties.getLeaseTime(), "leaseTime");
        this.retryInterval = normalizePositiveDuration(properties.getJdbcRetryInterval(), "jdbcRetryInterval");
    }

    /**
     * 使用 JDBC 分布式锁保护业务回调。
     *
     * @param lockKey  业务锁 key，会写入锁表 lock_key 字段
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
        String token = UUID.randomUUID().toString();
        boolean acquired = false;
        LockExecutionResult<T> result;
        try {
            acquired = acquireWithinWaitTime(lockKey, token, waitTime);
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
        return releaseLock(lockKey, token, acquired, result);
    }

    /**
     * 在给定等待时间内尝试获得 JDBC 锁。
     *
     * @param lockKey  业务锁 key
     * @param token    本次抢锁令牌
     * @param waitTime 最大等待时间
     * @return true 表示成功获得锁，false 表示超时或当前锁仍未过期
     * @throws InterruptedException 当前线程在等待重试时被中断
     */
    private boolean acquireWithinWaitTime(String lockKey, String token, Duration waitTime) throws InterruptedException {
        if (waitTime.isNegative()) {
            throw new IllegalArgumentException("waitTime must not be negative");
        }
        long waitMillis = waitTime.toMillis();
        long deadline = System.currentTimeMillis() + waitMillis;
        do {
            if (tryAcquireOnce(lockKey, token)) {
                return true;
            }
            if (waitMillis <= 0 || System.currentTimeMillis() >= deadline) {
                return false;
            }
            Thread.sleep(nextSleepMillis(deadline));
        } while (true);
    }

    /**
     * 尝试执行一次 JDBC 抢锁。
     *
     * <p>先尝试插入新锁记录；如果主键冲突，说明锁已存在，再尝试接管已经过期的锁记录。</p>
     *
     * @param lockKey 业务锁 key
     * @param token   本次抢锁令牌
     * @return true 表示本次尝试已经获得锁
     */
    private boolean tryAcquireOnce(String lockKey, String token) {
        Instant now = Instant.now();
        Instant expireAt = now.plus(leaseTime);
        try {
            return insertLock(lockKey, token, now, expireAt);
        } catch (DuplicateKeyException ex) {
            return updateExpiredLock(lockKey, token, now, expireAt);
        }
    }

    /**
     * 插入新的锁记录。
     *
     * @param lockKey  业务锁 key
     * @param token    本次抢锁令牌
     * @param now      当前时间
     * @param expireAt 过期时间
     * @return true 表示插入成功并获得锁
     */
    private boolean insertLock(String lockKey, String token, Instant now, Instant expireAt) {
        int affectedRows = jdbcOperations.update(
                "INSERT INTO " + tableName + " (lock_key, lock_token, expire_at, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
                lockKey,
                token,
                Timestamp.from(expireAt),
                Timestamp.from(now),
                Timestamp.from(now)
        );
        return affectedRows == 1;
    }

    /**
     * 接管已经过期的锁记录。
     *
     * @param lockKey  业务锁 key
     * @param token    本次抢锁令牌
     * @param now      当前时间
     * @param expireAt 新的过期时间
     * @return true 表示成功接管过期锁
     */
    private boolean updateExpiredLock(String lockKey, String token, Instant now, Instant expireAt) {
        int affectedRows = jdbcOperations.update(
                "UPDATE " + tableName + " SET lock_token = ?, expire_at = ?, updated_at = ? WHERE lock_key = ? AND expire_at <= ?",
                token,
                Timestamp.from(expireAt),
                Timestamp.from(now),
                lockKey,
                Timestamp.from(now)
        );
        return affectedRows == 1;
    }

    /**
     * 释放 JDBC 锁并把释放异常合并到执行结果中。
     *
     * @param lockKey  业务锁 key
     * @param token    本次抢锁令牌
     * @param acquired 当前执行是否已经获得锁
     * @param result   业务执行结果
     * @param <T>      业务回调返回值类型
     * @return 释放完成后的锁执行结果
     */
    private <T> LockExecutionResult<T> releaseLock(String lockKey, String token, boolean acquired, LockExecutionResult<T> result) {
        if (!acquired) {
            return result;
        }
        try {
            jdbcOperations.update("DELETE FROM " + tableName + " WHERE lock_key = ? AND lock_token = ?", lockKey, token);
            return result;
        } catch (DataAccessException ex) {
            if (result.error() != null) {
                result.error().addSuppressed(ex);
                return result;
            }
            return LockExecutionResult.failed(true, ex);
        }
    }

    /**
     * 计算下一次重试前需要休眠的时间。
     *
     * @param deadline 等待截止时间戳，单位毫秒
     * @return 休眠毫秒数
     */
    private long nextSleepMillis(long deadline) {
        long remainingMillis = Math.max(1, deadline - System.currentTimeMillis());
        return Math.min(retryInterval.toMillis(), remainingMillis);
    }

    /**
     * 校验 JDBC 锁表名称。
     *
     * @param tableName 配置中的表名
     * @return 校验通过的表名
     */
    private String validateTableName(String tableName) {
        if (tableName == null || !tableName.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("jdbcTableName must only contain letters, numbers and underscores");
        }
        return tableName;
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
}
