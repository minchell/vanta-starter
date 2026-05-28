package com.vanta.starter.log.http;

import java.net.URI;
import java.util.Map;


/**
 * 可被日志系统读取的 HTTP 请求抽象。
 * <p>
 * 日志模型不直接依赖 Servlet API，而是通过该接口读取请求方法、URL、头、参数、请求体和客户端 IP。
 * 这样后续如果接入 WebFlux、网关或其他 HTTP 框架，只需要新增适配器，不需要改日志核心模型。
 * </p>
 */
public interface RecordableHttpRequest {

    /**
     * 读取 HTTP 请求方法。
     *
     * @return 例如 GET、POST、PUT、DELETE
     */
    String getMethod();

    /**
     * 读取完整请求 URL。
     *
     * @return 包含查询字符串的请求 URL
     */
    URI getUrl();

    /**
     * 读取请求路径。
     * <p>/foo/bar</p>
     *
     * @return 不包含协议、域名和查询字符串的请求路径
     * @since 2.10.0
     */
    String getPath();

    /**
     * 读取请求头。
     *
     * @return 请求头键值对
     */
    Map<String, String> getHeaders();

    /**
     * 读取请求体。
     * <p>
     * 实现类应只返回适合日志采集的内容，例如 JSON 字符串；二进制、文件上传和不可重复读取内容应返回 {@code null}。
     * </p>
     *
     * @return 可记录的请求体，无法安全记录时返回 {@code null}
     */
    String getBody();

    /**
     * 读取请求参数。
     *
     * @return query、form 或请求体转换后的参数字符串
     */
    String getParams();

    /**
     * 读取客户端 IP。
     *
     * @return 业务网关或 Servlet 工具解析后的客户端 IP
     */
    String getIp();
}
