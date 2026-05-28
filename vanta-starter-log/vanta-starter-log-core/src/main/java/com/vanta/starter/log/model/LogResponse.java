package com.vanta.starter.log.model;

import com.vanta.starter.log.enums.Include;
import com.vanta.starter.log.http.RecordableHttpResponse;

import java.util.Map;
import java.util.Set;


/**
 * 操作日志中的响应信息快照。
 * <p>
 * 该模型在日志结束阶段从 {@link RecordableHttpResponse} 中提取响应状态、响应头、响应体或响应参数，
 * 并按照 {@link Include} 集合决定实际保存哪些内容。
 * </p>
 */
public class LogResponse {

    /**
     * HTTP 响应状态码。
     * <p>例如 200、400、500。</p>
     */
    private Integer status;

    /**
     * 响应头快照。
     * <p>只有采集字段包含 {@link Include#RESPONSE_HEADERS} 时才会保存。</p>
     */
    private Map<String, String> headers;

    /**
     * 响应体 JSON 字符串。
     * <p>只有采集字段包含 {@link Include#RESPONSE_BODY} 且响应体可安全读取时才会保存。</p>
     */
    private String body;

    /**
     * 响应参数 JSON 字符串。
     * <p>只有未采集响应体且采集字段包含 {@link Include#RESPONSE_PARAM} 时才会保存。</p>
     */
    private String params;

    /**
     * 根据响应适配器和采集字段创建响应日志快照。
     *
     * @param response 可被日志系统读取的响应适配器
     * @param includes 本次操作日志需要采集的字段集合
     */
    public LogResponse(RecordableHttpResponse response, Set<Include> includes) {
        this.status = response.getStatus();
        this.headers = (includes.contains(Include.RESPONSE_HEADERS)) ? response.getHeaders() : null;
        if (includes.contains(Include.RESPONSE_BODY)) {
            this.body = response.getBody();
        } else if (includes.contains(Include.RESPONSE_PARAM)) {
            this.params = response.getParams();
        }
    }

    /**
     * 读取 HTTP 响应状态码。
     *
     * @return HTTP 响应状态码
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置 HTTP 响应状态码。
     *
     * @param status HTTP 响应状态码
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 读取响应头快照。
     *
     * @return 响应头键值对，未采集时为 {@code null}
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 设置响应头快照。
     *
     * @param headers 响应头键值对
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * 读取响应体 JSON 字符串。
     *
     * @return 响应体 JSON 字符串，未采集时为 {@code null}
     */
    public String getBody() {
        return body;
    }

    /**
     * 设置响应体 JSON 字符串。
     *
     * @param body 响应体 JSON 字符串
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 读取响应参数 JSON 字符串。
     *
     * @return 响应参数 JSON 字符串，未采集时为 {@code null}
     */
    public String getParams() {
        return params;
    }

    /**
     * 设置响应参数 JSON 字符串。
     *
     * @param params 响应参数 JSON 字符串
     */
    public void setParams(String params) {
        this.params = params;
    }
}
