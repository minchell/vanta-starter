package com.vanta.starter.trace.filter;

import cn.hutool.core.text.CharSequenceUtil;
import com.vanta.starter.trace.autoconfigure.TraceProperties;
import com.vanta.starter.trace.handler.TLogWebCommon;
import com.yomahub.tlog.context.TLogContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


/**
 * TLogServletFilter 类。
 * <p>该类型属于 日志能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class TLogServletFilter implements Filter {

    /**
     * traceProperties 字段。
     * <p>用于保存 日志能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final TraceProperties traceProperties;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param traceProperties traceProperties 参数，调用方应传入与 日志能力 场景匹配的有效值
     */
    public TLogServletFilter(TraceProperties traceProperties) {
        this.traceProperties = traceProperties;
    }

    /**
     * 执行 doFilter 逻辑。
     * 该方法属于 日志能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param request  request 参数，调用方应传入与 日志能力 场景匹配的有效值
     * @param response response 参数，调用方应传入与 日志能力 场景匹配的有效值
     * @param chain    chain 参数，调用方应传入与 日志能力 场景匹配的有效值
     * @throws IOException      当底层客户端、配置解析或远程调用失败时抛出
     * @throws ServletException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpServletRequest && response instanceof HttpServletResponse httpServletResponse) {
            try {
                TLogWebCommon.loadInstance().preHandle(httpServletRequest);
                // 把 traceId 放入 response 的 header，为了方便有些人有这样的需求，从前端拿整条链路的 traceId
                String traceIdName = traceProperties.getTraceIdName();
                if (CharSequenceUtil.isNotBlank(traceIdName)) {
                    httpServletResponse.addHeader(traceIdName, TLogContext.getTraceId());
                }
                chain.doFilter(request, response);
            } finally {
                TLogWebCommon.loadInstance().afterCompletion();
            }
            return;
        }
        chain.doFilter(request, response);
    }
}
