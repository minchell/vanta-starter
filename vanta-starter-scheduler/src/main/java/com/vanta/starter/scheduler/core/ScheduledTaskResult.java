package com.vanta.starter.scheduler.core;

import java.time.Duration;

/**
 * 调度任务执行结果。
 *
 * @param success  是否执行成功
 * @param taskName 任务名称
 * @param duration 执行耗时
 * @param error    失败异常
 */
public record ScheduledTaskResult(
        boolean success,
        String taskName,
        Duration duration,
        Throwable error
) {

    /**
     * 创建任务执行成功结果。
     *
     * @param taskName 任务名称
     * @param duration 任务执行耗时
     * @return success 为 true、error 为空的任务执行结果
     */
    public static ScheduledTaskResult success(String taskName, Duration duration) {
        return new ScheduledTaskResult(true, taskName, duration, null);
    }

    /**
     * 创建任务执行失败结果。
     *
     * @param taskName 任务名称
     * @param duration 任务执行耗时
     * @param error    任务执行过程中抛出的异常
     * @return success 为 false、error 不为空的任务执行结果
     */
    public static ScheduledTaskResult failure(String taskName, Duration duration, Throwable error) {
        return new ScheduledTaskResult(false, taskName, duration, error);
    }
}
