package com.vanta.starter.web.autoconfigure.response;

import com.feiniaojin.gracefulresponse.advice.lifecycle.exception.BeforeControllerAdviceProcess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

/**
 * 默认异常处理前置回调。
 * <p>
 * graceful-response 在进入具体异常响应处理前会调用该组件。
 * 当前默认实现只根据配置打印请求方法、请求路径和异常堆栈，不改变异常响应内容。
 * </p>
 */
public class DefaultBeforeControllerAdviceProcessImpl implements BeforeControllerAdviceProcess {

    /**
     * 当前处理器日志记录器。
     */
    private final Logger log = LoggerFactory.getLogger(DefaultBeforeControllerAdviceProcessImpl.class);

    /**
     * 全局响应配置属性。
     */
    private final GlobalResponseProperties globalResponseProperties;

    /**
     * 创建默认异常处理前置回调。
     *
     * @param globalResponseProperties 全局响应配置属性。
     */
    public DefaultBeforeControllerAdviceProcessImpl(GlobalResponseProperties globalResponseProperties) {
        this.globalResponseProperties = globalResponseProperties;
    }

    /**
     * 执行异常处理前置回调。
     *
     * @param request  HTTP 请求。
     * @param response HTTP 响应。
     * @param handler  当前处理器，可能为空。
     * @param e        当前待处理异常。
     */
    @Override
    public void call(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception e) {
        if (globalResponseProperties.isPrintExceptionInGlobalAdvice()) {
            log.error("[{}] {}", request.getMethod(), request.getRequestURI(), e);
        }
    }
}
