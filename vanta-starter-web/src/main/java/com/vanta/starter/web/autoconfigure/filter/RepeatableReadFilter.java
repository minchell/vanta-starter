package com.vanta.starter.web.autoconfigure.filter;

import cn.hutool.core.util.StrUtil;
import com.vanta.starter.web.wrapper.RepeatReadRequestWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * 可重复读取请求过滤器。
 * <p>
 * 该过滤器只包装 JSON 请求，把原始 {@link HttpServletRequest} 替换为 {@link RepeatReadRequestWrapper}，
 * 使后续过滤器、拦截器和控制器可以重复读取请求体。
 * 非 JSON 请求直接放行，避免影响文件上传、表单提交等特殊请求类型。
 * </p>
 */
public class RepeatableReadFilter implements Filter {

    /**
     * 执行过滤逻辑。
     *
     * @param request  Servlet 请求。
     * @param response Servlet 响应。
     * @param chain    过滤器链。
     * @throws IOException      读取请求或执行后续过滤器失败时抛出。
     * @throws ServletException 执行后续过滤器失败时抛出。
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest
                && StrUtil.startWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            // 只对POST、PUT、PATCH等有请求体的请求进行包装
            chain.doFilter(new RepeatReadRequestWrapper(httpRequest), response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
