package com.vanta.starter.scheduler.core;

import java.time.Duration;
import java.time.Instant;

/**
 * 本地同步任务执行器。
 *
 * <p>该实现直接在当前线程执行任务，适合最小接入、单元测试和后续远程调度实现的默认基线。</p>
 */
public class LocalScheduledTaskExecutor implements ScheduledTaskExecutor {

    /**
     * 在当前线程同步执行任务。
     *
     * @param command 任务命令，提供任务名称、来源和元数据
     * @param task    任务逻辑
     * @return 执行结果；任务抛出异常时不会继续向外抛，而是记录到 ScheduledTaskResult.error
     */
    @Override
    public ScheduledTaskResult execute(ScheduledTaskCommand command, Runnable task) {
        Instant startedAt = Instant.now();
        try {
            task.run();
            return ScheduledTaskResult.success(command.taskName(), Duration.between(startedAt, Instant.now()));
        } catch (Exception ex) {
            return ScheduledTaskResult.failure(command.taskName(), Duration.between(startedAt, Instant.now()), ex);
        }
    }
}
