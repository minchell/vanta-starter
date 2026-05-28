package com.vanta.starter.log.http.servlet;

import cn.hutool.json.JSONUtil;
import com.vanta.starter.log.http.RecordableHttpResponse;
import com.vanta.starter.web.utils.ServletUtils;
import com.vanta.starter.web.wrapper.RepeatReadResponseWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.WebUtils;

import java.util.Map;


/**
 * Servlet 响应到日志响应抽象的适配器。
 * <p>
 * 该类把 {@link HttpServletResponse} 转换为 {@link RecordableHttpResponse}，负责读取状态码、响应头和 JSON 响应体。
 * 响应体读取依赖 {@link RepeatReadResponseWrapper}，没有包装或响应属于流式输出时不会强读内容。
 * </p>
 */
public final class RecordableServletHttpResponse implements RecordableHttpResponse {

    /**
     * 原始 Servlet 响应对象。
     * <p>
     * 适配器只从该对象读取日志所需信息，不修改响应头、响应体或提交状态。
     * </p>
     */
    private final HttpServletResponse response;
    /**
     * 创建适配器时捕获的 HTTP 状态码。
     * <p>
     * 提前保存状态码可以避免后续响应对象状态变化影响日志记录。
     * </p>
     */
    private final int status;

    /**
     * 创建 Servlet 响应适配器。
     *
     * @param response 当前 HTTP 响应，不能为 {@code null}
     */
    public RecordableServletHttpResponse(HttpServletResponse response) {
        this.response = response;
        this.status = response.getStatus();
    }

    /**
     * 读取 HTTP 响应状态码。
     *
     * @return 创建适配器时捕获的响应状态码
     */
    @Override
    public int getStatus() {
        return this.status;
    }

    /**
     * 读取响应头。
     *
     * @return 响应头键值对
     */
    @Override
    public Map<String, String> getHeaders() {
        return ServletUtils.getHeaderMap(response);
    }

    /**
     * 读取 JSON 响应体。
     * <p>
     * 只有响应已经被 {@link RepeatReadResponseWrapper} 包装、不是流式响应，并且内容是 JSON 时才返回正文。
     * </p>
     *
     * @return 可安全记录的 JSON 响应体，无法读取时返回 {@code null}
     */
    @Override
    public String getBody() {
        try {
            RepeatReadResponseWrapper wrappedResponse = WebUtils
                    .getNativeResponse(response, RepeatReadResponseWrapper.class);
            if (wrappedResponse == null || wrappedResponse.isStreamingResponse()) {
                return null;
            }
            String body = wrappedResponse.getResponseContent();
            return JSONUtil.isTypeJSON(body) ? body : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取响应参数。
     * <p>
     * 当前响应参数与响应体语义一致，保留该方法是为了和请求侧接口结构对齐。
     * </p>
     *
     * @return JSON 响应体，无法读取时返回 {@code null}
     */
    @Override
    public String getParams() {
        return this.getBody();
    }
}
