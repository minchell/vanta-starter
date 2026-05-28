package com.vanta.starter.scheduler.core;

/**
 * 调度任务执行器。
 *
 * <p>接口只负责执行任务，不负责定义 cron、持久化或远程调度协议。</p>
 */
public interface ScheduledTaskExecutor {

    /**
     * 执行任务。
     *
     * @param command 任务命令
     * @param task    真实任务逻辑；该 Runnable 应只包含业务执行逻辑，不应在内部吞掉关键异常
     * @return 执行结果，包含成功状态、耗时和异常信息
     */
    ScheduledTaskResult execute(ScheduledTaskCommand command, Runnable task);
}
