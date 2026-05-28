package com.vanta.starter.lock.core;

import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * 分布式锁统一模板。
 *
 * <p>业务代码只依赖该接口，不直接绑定 Local、Redis、Zookeeper 或 JDBC 的具体实现。
 * 这样业务系统可以通过配置或自定义 Bean 切换锁后端，而不需要改动业务调用点。</p>
 */
public interface DistributedLockTemplate {

    /**
     * 在锁保护下执行业务任务。
     *
     * @param lockKey  业务锁 key，同一个实现内相同 key 互斥执行
     * @param waitTime 等待获取锁的最长时间，超时未获取锁时返回 notAcquired
     * @param callback 获取锁后执行的业务逻辑
     * @param <T>      业务逻辑返回值类型
     * @return 锁执行结果，包含是否获得锁、业务返回值和异常信息
     */
    <T> LockExecutionResult<T> execute(String lockKey, Duration waitTime, Callable<T> callback);
}
