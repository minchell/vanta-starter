package com.vanta.starter.observability.core;

import java.util.Optional;

/**
 * 观测上下文持有器。
 *
 * <p>使用 ThreadLocal 保存当前线程上下文。跨线程传播需要业务或后续 executor 适配模块显式处理，
 * starter 不会隐式修改线程池行为。</p>
 */
public class ObservationContextHolder {

    /**
     * 当前线程绑定的观测上下文。
     *
     * <p>这里刻意使用普通 ThreadLocal，而不是全局静态变量，原因是 starter 可能被多个应用、
     * 多个测试上下文或多个自定义 Bean 同时使用。实例级 ThreadLocal 更容易被业务替换和测试。</p>
     */
    private final ThreadLocal<ObservationContext> holder = new ThreadLocal<>();

    /**
     * 绑定当前线程的观测上下文。
     *
     * @param context 当前请求、任务或消息消费过程的观测上下文；传入 null 会清空当前线程上下文
     */
    public void set(ObservationContext context) {
        if (context == null) {
            clear();
            return;
        }
        holder.set(context);
    }

    /**
     * 获取当前线程的观测上下文。
     *
     * @return 当前线程存在上下文时返回 Optional 包装值；没有上下文时返回 Optional.empty()
     */
    public Optional<ObservationContext> get() {
        return Optional.ofNullable(holder.get());
    }

    /**
     * 清理当前线程的观测上下文。
     *
     * <p>Web 请求、消息消费和任务执行结束后必须调用该方法，避免线程池复用时把上一次调用的
     * traceId、spanId 或租户字段泄漏到下一次调用。</p>
     */
    public void clear() {
        holder.remove();
    }
}
