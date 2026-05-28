package com.vanta.starter.log.filter;

import cn.hutool.extra.spring.SpringUtil;
import com.vanta.starter.log.model.LogProperties;
import com.vanta.starter.web.wrapper.RepeatReadRequestWrapper;
import com.vanta.starter.web.wrapper.RepeatReadResponseWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * 为日志采集准备可重复读取请求和响应的 Servlet 过滤器。
 * <p>
 * 操作日志和访问日志需要在请求结束后读取 body 和响应体。Servlet 原生流只能读取一次，
 * 因此该过滤器会在非排除路径上包裹可重复读取的 request/response，并在过滤链结束后把缓存响应写回客户端。
 * </p>
 */
public class LogFilter extends OncePerRequestFilter {

    /**
     * 日志 starter 的配置属性。
     * <p>
     * 过滤器通过它判断当前 URI 是否需要跳过日志包装，避免静态资源、错误页或业务排除路径产生额外开销。
     * </p>
     */
    private final LogProperties logProperties;

    /**
     * 创建日志过滤器。
     *
     * @param logProperties 日志采集配置，不能为 {@code null}
     */
    public LogFilter(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    /**
     * 对需要采集日志的请求包裹可重复读取的 request/response。
     *
     * @param request     当前 HTTP 请求
     * @param response    当前 HTTP 响应
     * @param filterChain Servlet 过滤器链
     * @throws ServletException 过滤链执行失败时抛出
     * @throws IOException      读取或写回请求响应流失败时抛出
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (this.isNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 包装可重复读取请求及响应
        RepeatReadRequestWrapper wrappedRequest = request instanceof RepeatReadRequestWrapper wrapped
                ? wrapped
                : new RepeatReadRequestWrapper(request);
        RepeatReadResponseWrapper wrappedResponse = response instanceof RepeatReadResponseWrapper wrapped
                ? wrapped
                : new RepeatReadResponseWrapper(response);
        filterChain.doFilter(wrappedRequest, wrappedResponse);

        // 复制缓存数据到原始响应
        wrappedResponse.copyBodyToResponse();
    }

    /**
     * 判断当前请求是否不需要日志包装。
     * <p>
     * 非法 URL、Spring Boot 错误页和配置排除路径都会直接放行，避免日志过滤器干扰框架错误处理。
     * </p>
     *
     * @param request 当前 HTTP 请求
     * @return {@code true} 表示直接放行；{@code false} 表示需要包裹可重复读取对象
     */
    private boolean isNotFilter(HttpServletRequest request) {
        if (!isRequestValid(request)) {
            return true;
        }
        // 不拦截 /error
        ServerProperties serverProperties = SpringUtil.getBean(ServerProperties.class);
        if (request.getRequestURI().equals(serverProperties.getError().getPath())) {
            return true;
        }
        // 放行路径
        return logProperties.isMatchExcludeUri(request.getRequestURI());
    }

    /**
     * 校验请求 URL 是否能被解析成标准 URI。
     *
     * @param request 当前 HTTP 请求
     * @return {@code true} 表示请求 URL 合法；{@code false} 表示 URL 语法异常
     */
    private boolean isRequestValid(HttpServletRequest request) {
        try {
            new URI(request.getRequestURL().toString());
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
