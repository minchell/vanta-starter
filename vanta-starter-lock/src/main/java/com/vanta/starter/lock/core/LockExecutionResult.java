package com.vanta.starter.lock.core;

/**
 * 锁执行结果。
 *
 * @param acquired 是否成功获得锁
 * @param value    业务回调返回值
 * @param error    业务异常、锁实现异常或释放异常
 * @param <T>      业务回调返回值类型
 */
public record LockExecutionResult<T>(boolean acquired, T value, Throwable error) {

    /**
     * 创建成功获得锁且业务正常返回的结果。
     *
     * @param value 业务回调返回值
     * @param <T>   业务回调返回值类型
     * @return acquired 为 true、error 为空的锁执行结果
     */
    public static <T> LockExecutionResult<T> acquired(T value) {
        return new LockExecutionResult<>(true, value, null);
    }

    /**
     * 创建未获得锁的结果。
     *
     * <p>该结果用于表达锁竞争失败，不代表业务异常。调用方可以根据 acquired=false
     * 决定重试、降级或直接返回。</p>
     *
     * @param <T> 业务回调返回值类型
     * @return acquired 为 false 的锁执行结果
     */
    public static <T> LockExecutionResult<T> notAcquired() {
        return new LockExecutionResult<>(false, null, null);
    }

    /**
     * 创建已获得锁但业务执行失败的结果。
     *
     * @param error 业务回调抛出的异常
     * @param <T>   业务回调返回值类型
     * @return acquired 为 true、error 不为空的锁执行结果
     */
    public static <T> LockExecutionResult<T> failed(Throwable error) {
        return new LockExecutionResult<>(true, null, error);
    }

    /**
     * 创建锁执行失败结果。
     *
     * <p>该方法用于区分“获取锁之前失败”和“获取锁之后失败”：例如 Redis 连接异常可能发生在获取锁之前，
     * 此时 acquired 应为 false；业务回调异常发生在获取锁之后，此时 acquired 应为 true。</p>
     *
     * @param acquired 失败发生时是否已经获得锁
     * @param error    锁实现异常、业务异常或释放异常
     * @param <T>      业务回调返回值类型
     * @return 带有明确 acquired 状态的失败结果
     */
    public static <T> LockExecutionResult<T> failed(boolean acquired, Throwable error) {
        return new LockExecutionResult<>(acquired, null, error);
    }
}
