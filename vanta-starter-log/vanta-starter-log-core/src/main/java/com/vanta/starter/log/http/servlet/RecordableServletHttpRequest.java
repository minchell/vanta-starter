package com.vanta.starter.log.http.servlet;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.log.http.RecordableHttpRequest;
import com.vanta.starter.web.utils.ServletUtils;
import com.vanta.starter.web.wrapper.RepeatReadRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * Servlet 请求到日志请求抽象的适配器。
 * <p>
 * 该类把 {@link HttpServletRequest} 转换为 {@link RecordableHttpRequest}，负责从 Servlet 容器读取请求方法、
 * URL、请求头、JSON 请求体、参数和客户端 IP。请求体读取依赖 {@link RepeatReadRequestWrapper}，没有包装时不会强读原始流。
 * </p>
 */
public final class RecordableServletHttpRequest implements RecordableHttpRequest {

    /**
     * 原始 Servlet 请求对象。
     * <p>
     * 适配器只从该对象读取日志所需信息，不修改请求属性和业务参数。
     * </p>
     */
    private final HttpServletRequest request;

    /**
     * 创建 Servlet 请求适配器。
     *
     * @param request 当前 HTTP 请求，不能为 {@code null}
     */
    public RecordableServletHttpRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 读取 HTTP 请求方法。
     *
     * @return 例如 GET、POST、PUT、DELETE
     */
    @Override
    public String getMethod() {
        return request.getMethod();
    }

    /**
     * 读取完整请求 URL。
     * <p>
     * 当原始查询字符串包含特殊字符导致 URI 构造失败时，会先按 UTF-8 编码查询字符串再创建 URI。
     * </p>
     *
     * @return 包含查询字符串的请求 URL
     */
    @Override
    public URI getUrl() {
        String queryString = request.getQueryString();
        if (CharSequenceUtil.isBlank(queryString)) {
            return URI.create(request.getRequestURL().toString());
        }
        try {
            StringBuilder urlBuilder = this.appendQueryString(queryString);
            return new URI(urlBuilder.toString());
        } catch (URISyntaxException e) {
            String encoded = UriUtils.encodeQuery(queryString, StandardCharsets.UTF_8);
            StringBuilder urlBuilder = this.appendQueryString(encoded);
            return URI.create(urlBuilder.toString());
        }
    }

    /**
     * 读取请求路径。
     *
     * @return 不包含协议、域名和查询字符串的请求 URI
     */
    @Override
    public String getPath() {
        return request.getRequestURI();
    }

    /**
     * 读取请求头。
     *
     * @return 请求头键值对
     */
    @Override
    public Map<String, String> getHeaders() {
        return ServletUtils.getHeaderMap(request);
    }

    /**
     * 读取 JSON 请求体。
     * <p>
     * 只有请求已经被 {@link RepeatReadRequestWrapper} 包装、不是 multipart，并且内容是 JSON 时才返回正文；
     * 其他情况返回 {@code null}，避免日志采集消耗原始请求流或记录文件上传内容。
     * </p>
     *
     * @return 可安全记录的 JSON 请求体，无法读取时返回 {@code null}
     */
    @Override
    public String getBody() {
        try {
            RepeatReadRequestWrapper wrappedRequest = WebUtils
                    .getNativeRequest(request, RepeatReadRequestWrapper.class);
            if (wrappedRequest == null || ServletUtils.isMultipart(request)) {
                return null;
            }
            String body = wrappedRequest.getContentAsString();
            return JSONUtil.isTypeJSON(body) ? body : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取请求参数。
     * <p>
     * JSON 请求体优先作为参数输出；没有 JSON 请求体时，回退到 Servlet 参数 Map 的 JSON 表示。
     * </p>
     *
     * @return 请求参数 JSON 字符串
     */
    @Override
    public String getParams() {
        String body = this.getBody();
        return CharSequenceUtil.isNotBlank(body) ? body : JSONUtil.toJsonStr(ServletUtils.getParamMap(request));
    }

    /**
     * 读取客户端 IP。
     *
     * @return 通过 Servlet 工具解析出的客户端 IP
     */
    @Override
    public String getIp() {
        return ServletUtils.getClientIP(request);
    }

    /**
     * 把查询字符串追加到请求 URL 后。
     *
     * @param queryString 原始或编码后的查询字符串
     * @return 带查询字符串的 URL 构建器
     */
    private StringBuilder appendQueryString(String queryString) {
        return new StringBuilder().append(request.getRequestURL())
                .append(StringConstants.QUESTION_MARK)
                .append(queryString);
    }
}
