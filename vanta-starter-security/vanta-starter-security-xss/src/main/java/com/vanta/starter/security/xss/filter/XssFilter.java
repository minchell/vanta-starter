package com.vanta.starter.security.xss.filter;

import cn.hutool.core.collection.CollUtil;
import com.vanta.starter.core.util.SpringUtils;
import com.vanta.starter.security.xss.autoconfigure.XssProperties;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * XSS 过滤器
 */
public class XssFilter implements Filter {

    /**
     * log 字段。
     * <p>用于保存 安全防护能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(XssFilter.class);

    /**
     * xssProperties 字段。
     * <p>用于保存 安全防护能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final XssProperties xssProperties;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param xssProperties xssProperties 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    public XssFilter(XssProperties xssProperties) {
        this.xssProperties = xssProperties;
    }

    /**
     * 执行 init 逻辑。
     * 该方法属于 安全防护能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param filterConfig filterConfig 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     */
    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("[Vanta Starter] - Auto Configuration 'Web-XssFilter' completed initialization.");
    }

    /**
     * 执行 doFilter 逻辑。
     * 该方法属于 安全防护能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param servletRequest  servletRequest 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @param servletResponse servletResponse 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @param filterChain     filterChain 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @throws IOException      当底层客户端、配置解析或远程调用失败时抛出
     * @throws ServletException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        // 未开启 XSS 过滤，则直接跳过
        if (servletRequest instanceof HttpServletRequest request && xssProperties.isEnabled()) {
            // 放行路由：忽略 XSS 过滤
            List<String> excludePatterns = xssProperties.getExcludePatterns();
            if (CollUtil.isNotEmpty(excludePatterns) && SpringUtils.isMatch(request
                    .getServletPath(), excludePatterns)) {
                filterChain.doFilter(request, servletResponse);
                return;
            }
            // 拦截路由：执行 XSS 过滤
            List<String> includePatterns = xssProperties.getIncludePatterns();
            if (CollUtil.isNotEmpty(includePatterns)) {
                if (SpringUtils.isMatch(request.getServletPath(), includePatterns)) {
                    filterChain.doFilter(new XssServletRequestWrapper(request, xssProperties), servletResponse);
                } else {
                    filterChain.doFilter(request, servletResponse);
                }
                return;
            }
            // 默认：执行 XSS 过滤
            filterChain.doFilter(new XssServletRequestWrapper(request, xssProperties), servletResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
