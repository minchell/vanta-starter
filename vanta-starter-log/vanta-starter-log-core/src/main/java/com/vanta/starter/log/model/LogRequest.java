package com.vanta.starter.log.model;

import cn.hutool.core.text.CharSequenceUtil;
import com.vanta.starter.core.util.ExceptionUtils;
import com.vanta.starter.core.util.IpUtils;
import com.vanta.starter.log.enums.Include;
import com.vanta.starter.log.http.RecordableHttpRequest;
import com.vanta.starter.web.utils.ServletUtils;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.util.Map;
import java.util.Set;


/**
 * 操作日志中的请求信息快照。
 * <p>
 * 该模型在日志结束阶段从 {@link RecordableHttpRequest} 中提取需要采集的字段，
 * 并按照 {@link Include} 集合决定是否记录请求头、请求体、请求参数、IP 归属地、浏览器和操作系统。
 * 它只保存日志快照，不反向修改原始 HTTP 请求。
 * </p>
 */
public class LogRequest {

    /**
     * HTTP 请求方法。
     * <p>例如 GET、POST、PUT、DELETE。</p>
     */
    private String method;

    /**
     * 完整请求 URL。
     * <p>包含协议、域名、路径和查询字符串。</p>
     */
    private URI url;

    /**
     * 客户端 IP。
     * <p>由请求适配器按业务网关规则解析。</p>
     */
    private String ip;

    /**
     * 请求头快照。
     * <p>只有采集字段包含 {@link Include#REQUEST_HEADERS} 时才会保存。</p>
     */
    private Map<String, String> headers;

    /**
     * 请求体 JSON 字符串。
     * <p>只有采集字段包含 {@link Include#REQUEST_BODY} 且请求体可安全读取时才会保存。</p>
     */
    private String body;

    /**
     * 请求参数 JSON 字符串。
     * <p>只有未采集请求体且采集字段包含 {@link Include#REQUEST_PARAM} 时才会保存。</p>
     */
    private String params;

    /**
     * 客户端 IP 归属地。
     * <p>只有采集字段包含 {@link Include#IP_ADDRESS} 时才会尝试解析；解析失败时保持 {@code null}。</p>
     */
    private String address;

    /**
     * 浏览器名称。
     * <p>从 User-Agent 请求头解析，只有采集字段包含 {@link Include#BROWSER} 时才会保存。</p>
     */
    private String browser;

    /**
     * 操作系统名称。
     * <p>从 User-Agent 请求头解析，只有采集字段包含 {@link Include#OS} 时才会保存。</p>
     */
    private String os;

    /**
     * 根据请求适配器和采集字段创建请求日志快照。
     *
     * @param request  可被日志系统读取的请求适配器
     * @param includes 本次操作日志需要采集的字段集合
     */
    public LogRequest(RecordableHttpRequest request, Set<Include> includes) {
        this.method = request.getMethod();
        this.url = request.getUrl();
        this.ip = request.getIp();
        this.headers = (includes.contains(Include.REQUEST_HEADERS)) ? request.getHeaders() : null;
        if (includes.contains(Include.REQUEST_BODY)) {
            this.body = request.getBody();
        } else if (includes.contains(Include.REQUEST_PARAM)) {
            this.params = request.getParams();
        }
        this.address = (includes.contains(Include.IP_ADDRESS))
                ? ExceptionUtils.exToNull(() -> IpUtils.getIpv4Address(this.ip))
                : null;
        if (this.headers == null) {
            return;
        }
        String userAgentString = this.headers.entrySet()
                .stream()
                .filter(h -> HttpHeaders.USER_AGENT.equalsIgnoreCase(h.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
        if (CharSequenceUtil.isNotBlank(userAgentString)) {
            this.browser = (includes.contains(Include.BROWSER)) ? ServletUtils.getBrowser(userAgentString) : null;
            this.os = (includes.contains(Include.OS)) ? ServletUtils.getOs(userAgentString) : null;
        }
    }

    /**
     * 读取 HTTP 请求方法。
     *
     * @return HTTP 请求方法
     */
    public String getMethod() {
        return method;
    }

    /**
     * 设置 HTTP 请求方法。
     *
     * @param method HTTP 请求方法
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 读取完整请求 URL。
     *
     * @return 完整请求 URL
     */
    public URI getUrl() {
        return url;
    }

    /**
     * 设置完整请求 URL。
     *
     * @param url 完整请求 URL
     */
    public void setUrl(URI url) {
        this.url = url;
    }

    /**
     * 读取客户端 IP。
     *
     * @return 客户端 IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置客户端 IP。
     *
     * @param ip 客户端 IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 读取请求头快照。
     *
     * @return 请求头键值对
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 设置请求头快照。
     *
     * @param headers 请求头键值对
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * 读取请求体 JSON 字符串。
     *
     * @return 请求体 JSON 字符串，未采集时为 {@code null}
     */
    public String getBody() {
        return body;
    }

    /**
     * 设置请求体 JSON 字符串。
     *
     * @param body 请求体 JSON 字符串
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 读取请求参数 JSON 字符串。
     *
     * @return 请求参数 JSON 字符串，未采集时为 {@code null}
     */
    public String getParams() {
        return params;
    }

    /**
     * 设置请求参数 JSON 字符串。
     *
     * @param params 请求参数 JSON 字符串
     */
    public void setParams(String params) {
        this.params = params;
    }

    /**
     * 读取客户端 IP 归属地。
     *
     * @return IP 归属地，未采集或解析失败时为 {@code null}
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置客户端 IP 归属地。
     *
     * @param address IP 归属地
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 读取浏览器名称。
     *
     * @return 浏览器名称，未采集时为 {@code null}
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * 设置浏览器名称。
     *
     * @param browser 浏览器名称
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * 读取操作系统名称。
     *
     * @return 操作系统名称，未采集时为 {@code null}
     */
    public String getOs() {
        return os;
    }

    /**
     * 设置操作系统名称。
     *
     * @param os 操作系统名称
     */
    public void setOs(String os) {
        this.os = os;
    }
}
