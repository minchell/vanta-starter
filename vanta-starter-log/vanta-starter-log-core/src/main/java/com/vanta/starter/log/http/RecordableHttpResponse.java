package com.vanta.starter.log.http;

import java.util.Map;


/**
 * 可被日志系统读取的 HTTP 响应抽象。
 * <p>
 * 日志模型通过该接口读取响应状态、响应头和响应体，避免核心模型直接绑定 Servlet API。
 * 对不可重复读取、流式输出或非文本响应，实现类应返回 {@code null}，避免日志采集破坏业务响应。
 * </p>
 */
public interface RecordableHttpResponse {

    /**
     * 读取 HTTP 响应状态码。
     *
     * @return 例如 200、400、500
     */
    int getStatus();

    /**
     * 读取响应头。
     *
     * @return 响应头键值对
     */
    Map<String, String> getHeaders();

    /**
     * 读取响应体。
     * <p>
     * 实现类应只返回适合日志采集的内容，例如 JSON 字符串；流式响应或二进制内容应返回 {@code null}。
     * </p>
     *
     * @return 可记录的响应体，无法安全记录时返回 {@code null}
     */
    String getBody();

    /**
     * 读取响应参数。
     *
     * @return 当前默认等同于响应体
     */
    String getParams();
}
