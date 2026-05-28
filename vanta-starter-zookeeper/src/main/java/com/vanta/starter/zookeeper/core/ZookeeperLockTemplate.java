package com.vanta.starter.zookeeper.core;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Zookeeper 的分布式锁模板。
 *
 * <p>模板负责获取和释放锁，业务只需要提供锁路径、等待时间和临界区逻辑。
 * 锁获取失败会抛出 IllegalStateException，避免业务误以为任务已经执行。</p>
 */
public class ZookeeperLockTemplate {

    /**
     * CuratorFramework 客户端。
     * <p>模板通过该客户端创建 {@link InterProcessMutex}，业务方可以替换客户端 Bean 来接管连接策略。</p>
     */
    private final CuratorFramework curatorFramework;

    /**
     * 创建 Zookeeper 分布式锁模板。
     *
     * @param curatorFramework CuratorFramework 客户端
     */
    public ZookeeperLockTemplate(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    /**
     * 在分布式锁保护下执行任务。
     *
     * @param lockPath 锁路径
     * @param waitTime 等待获取锁的最长时间
     * @param callback 获得锁后执行的业务逻辑
     * @param <T>      返回值类型
     * @return 业务逻辑返回值
     */
    public <T> T execute(String lockPath, Duration waitTime, Callable<T> callback) {
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, lockPath);
        boolean acquired = false;
        try {
            acquired = mutex.acquire(waitTime.toMillis(), TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new IllegalStateException("failed to acquire zookeeper lock: " + lockPath);
            }
            return callback.call();
        } catch (Exception ex) {
            if (ex instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("execute zookeeper lock callback failed: " + lockPath, ex);
        } finally {
            if (acquired) {
                try {
                    mutex.release();
                } catch (Exception ex) {
                    throw new IllegalStateException("release zookeeper lock failed: " + lockPath, ex);
                }
            }
        }
    }
}
