package com.vanta.starter.core.constant;

import org.springframework.core.Ordered;

/**
 * Spring 组件顺序常量集合。
 * <p>
 * 该类集中维护过滤器、拦截器等链式组件的顺序值，避免多个 starter 各自随意指定 order 导致执行顺序不可预测。
 * 约定上，越靠近 {@link Ordered#HIGHEST_PRECEDENCE} 的组件越早执行，越靠近 {@link Ordered#LOWEST_PRECEDENCE} 的组件越晚执行。
 * </p>
 */
public class OrderedConstants {

    /**
     * 私有构造方法。
     * <p>
     * 顺序定义是纯常量集合，不允许通过实例承载额外状态。
     * </p>
     */
    private OrderedConstants() {
    }

    /**
     * 过滤器顺序
     */
    public static final class Filter {

        /**
         * API 加密过滤器顺序
         */
        public static final int API_ENCRYPT_FILTER = Ordered.HIGHEST_PRECEDENCE;

        /**
         * 链路追踪过滤器顺序
         */
        public static final int TRACE_FILTER = Ordered.HIGHEST_PRECEDENCE + 100;

        /**
         * XSS 过滤器顺序
         */
        public static final int XSS_FILTER = Ordered.HIGHEST_PRECEDENCE + 200;

        /**
         * 日志过滤器顺序
         */
        public static final int LOG_FILTER = Ordered.LOWEST_PRECEDENCE - 100;

        /**
         * 私有构造方法。
         * <p>
         * 过滤器顺序分组只作为命名空间使用，不应该被实例化。
         * </p>
         */
        private Filter() {
        }
    }

    /**
     * 拦截器顺序
     */
    public static final class Interceptor {

        /**
         * 租户拦截器顺序
         */
        public static final int TENANT_INTERCEPTOR = Ordered.HIGHEST_PRECEDENCE + 100;

        /**
         * 认证拦截器顺序
         */
        public static final int AUTH_INTERCEPTOR = Ordered.HIGHEST_PRECEDENCE + 200;

        /**
         * 日志拦截器顺序
         */
        public static final int LOG_INTERCEPTOR = Ordered.LOWEST_PRECEDENCE - 100;

        /**
         * 私有构造方法。
         * <p>
         * 拦截器顺序分组只作为命名空间使用，不应该被实例化。
         * </p>
         */
        private Interceptor() {
        }
    }
}
