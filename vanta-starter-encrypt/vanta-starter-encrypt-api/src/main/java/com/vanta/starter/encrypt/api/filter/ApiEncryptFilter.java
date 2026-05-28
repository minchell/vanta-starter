package com.vanta.starter.encrypt.api.filter;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.vanta.starter.encrypt.api.annotation.ApiEncrypt;
import com.vanta.starter.encrypt.api.autoconfigure.ApiEncryptProperties;
import com.vanta.starter.web.utils.WebSpringUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.Optional;

/**
 * API 加密过滤器
 */
public class ApiEncryptFilter implements Filter {

    /**
     * properties 字段。
     * <p>用于保存 加密能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final ApiEncryptProperties properties;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param properties properties 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public ApiEncryptFilter(ApiEncryptProperties properties) {
        this.properties = properties;
    }

    /**
     * 执行 doFilter 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param servletRequest  servletRequest 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param servletResponse servletResponse 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param chain           chain 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @throws IOException      当底层客户端、配置解析或远程调用失败时抛出
     * @throws ServletException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 是否加密响应
        boolean isResponseEncrypt = this.isResponseEncrypt(request);
        // 密钥标头
        String secretKeyHeader = properties.getSecretKeyHeader();
        ServletRequest requestWrapper = null;
        ServletResponse responseWrapper = null;
        ResponseBodyEncryptWrapper responseBodyEncryptWrapper = null;
        // 是否为 PUT 或者 POST 请求
        if (HttpMethod.PUT.matches(request.getMethod()) || HttpMethod.POST.matches(request.getMethod())) {
            // 获取密钥值
            String secretKeyValue = request.getHeader(secretKeyHeader);
            if (CharSequenceUtil.isNotBlank(secretKeyValue)) {
                // 请求解密
                requestWrapper = new RequestBodyDecryptWrapper(request, properties.getPrivateKey(), secretKeyHeader);
            }
        }
        // 响应加密，响应包装器替换响应体加密包装器
        if (isResponseEncrypt) {
            responseBodyEncryptWrapper = new ResponseBodyEncryptWrapper(response);
            responseWrapper = responseBodyEncryptWrapper;
        }
        // 继续执行
        chain.doFilter(ObjectUtil.defaultIfNull(requestWrapper, request), ObjectUtil.defaultIfNull(responseWrapper, response));
        // 响应加密，执行完成后，响应密文
        if (isResponseEncrypt) {
            servletResponse.reset();
            // 获取密文
            String encryptContent = responseBodyEncryptWrapper.getEncryptContent(response, properties.getPublicKey(), secretKeyHeader);
            // 写出密文
            servletResponse.getWriter().write(encryptContent);
        }
    }

    /**
     * 是否加密响应
     *
     * @param request 请求对象
     * @return 是否加密响应
     */
    private boolean isResponseEncrypt(HttpServletRequest request) {
        // 获取 API 加密注解
        ApiEncrypt apiEncrypt = Optional.ofNullable(WebSpringUtils.getHandlerMethod(request))
                .map(h -> h.getMethodAnnotation(ApiEncrypt.class))
                .orElse(null);
        return apiEncrypt != null && apiEncrypt.response();
    }
}
