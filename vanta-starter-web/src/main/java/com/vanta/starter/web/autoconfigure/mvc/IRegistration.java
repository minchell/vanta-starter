package com.vanta.starter.web.autoconfigure.mvc;

import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * Web MVC 拦截器注册扩展点。
 * <p>
 * 业务方实现该接口并注册为 Spring Bean 后，{@link WebMvcAutoConfiguration} 会自动把对应拦截器加入 MVC 拦截器链。
 * 默认方法均返回 {@code null}，表示使用框架默认路径、顺序和匹配器。
 * </p>
 */
public interface IRegistration {

    /**
     * 获取需要注册的处理器拦截器。
     *
     * @return 处理器拦截器；返回 {@code null} 时不会注册。
     */
    HandlerInterceptor getHandlerInterceptor();

    /**
     * 获取需要包含的路径模式。
     *
     * @return 包含路径列表；为空时默认匹配所有路径。
     */
    default List<String> getIncludePatterns() {
        return null;
    }

    /**
     * 获取需要排除的路径模式。
     *
     * @return 排除路径列表；为空时不排除。
     */
    default List<String> getExcludePatterns() {
        return null;
    }

    /**
     * 获取自定义路径匹配器。
     *
     * @return 路径匹配器；为空时使用 Spring 默认匹配器。
     */
    default PathMatcher getPathMatcher() {
        return null;
    }

    /**
     * 获取拦截器顺序。
     *
     * @return 拦截器顺序；为空时使用 Spring 默认顺序。
     */
    default Integer getOrder() {
        return null;
    }

}
