package com.vanta.starter.web.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.vanta.starter.core.constant.StringConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.Map;

/**
 * Servlet Web 环境辅助工具类。
 * <p>
 * 该类继承 Hutool 的 {@link JakartaServletUtil}，在其基础上补充 User-Agent 解析、当前请求上下文获取、
 * 响应头读取、JSON 写出以及请求/响应内容类型判断等能力。
 * 所有方法只依赖当前线程绑定的 Servlet 请求上下文，不会主动创建网络连接。
 * </p>
 */
public class ServletUtils extends JakartaServletUtil {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private ServletUtils() {
    }

    /**
     * 获取浏览器及其版本信息
     *
     * @param request 请求对象
     * @return 浏览器及其版本信息；请求为空或无法解析时返回 {@code null}。
     */
    public static String getBrowser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return getBrowser(request.getHeader("User-Agent"));
    }

    /**
     * 获取浏览器及其版本信息
     *
     * @param userAgentString User-Agent 字符串
     * @return 浏览器及其版本信息；无法解析时返回 {@code null}。
     */
    public static String getBrowser(String userAgentString) {
        try {
            UserAgent userAgent = UserAgentUtil.parse(userAgentString);
            if (userAgent == null || userAgent.getBrowser() == null) {
                return null;
            }
            String browserName = userAgent.getBrowser().getName();
            String version = userAgent.getVersion();
            return CharSequenceUtil.isBlank(version) ? browserName : browserName + StringConstants.SPACE + version;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取操作系统
     *
     * @param request 请求对象
     * @return 操作系统名称；请求为空或无法解析时返回 {@code null}。
     */
    public static String getOs(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return getOs(request.getHeader("User-Agent"));
    }

    /**
     * 获取操作系统
     *
     * @param userAgentString User-Agent 字符串
     * @return 操作系统名称；无法解析时返回 {@code null}。
     */
    public static String getOs(String userAgentString) {
        try {
            UserAgent userAgent = UserAgentUtil.parse(userAgentString);
            if (userAgent == null || userAgent.getOs() == null) {
                return null;
            }
            return userAgent.getOs().getName();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取响应所有的头（header）信息
     *
     * @param response 响应对象 {@link HttpServletResponse}。
     * @return 响应头 Map，key 为 header 名称，value 为 header 值。
     */
    public static Map<String, String> getHeaderMap(HttpServletResponse response) {
        final Collection<String> headerNames = response.getHeaderNames();
        final Map<String, String> headerMap = MapUtil.newHashMap(headerNames.size(), true);
        for (String name : headerNames) {
            headerMap.put(name, response.getHeader(name));
        }
        return headerMap;
    }

    /**
     * 获取 HTTP Session
     *
     * @return 当前请求会话；当前线程没有请求上下文时返回 {@code null}。
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getSession() : null;
    }

    /**
     * 获取 HTTP Request
     *
     * @return 当前线程绑定的 HTTP 请求；没有请求上下文时返回 {@code null}。
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest();
    }

    /**
     * 获取 HTTP Response
     *
     * @return 当前线程绑定的 HTTP 响应；没有请求上下文时返回 {@code null}。
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getResponse();
    }

    /**
     * 获取请求属性
     *
     * @return Servlet 请求属性；当前线程没有绑定 Servlet 请求时返回 {@code null}。
     */
    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 响应 JSON 数据给客户端
     *
     * @param response 响应对象
     * @param data     响应数据
     * @see #write(HttpServletResponse, String, String)
     */
    public static void writeJSON(HttpServletResponse response, String data) {
        write(response, data, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 检查请求是否为 {@code multipart/form-data} 格式（常用于文件上传）
     *
     * @param request 请求对象
     * @return {@code true} 表示 multipart 请求，{@code false} 表示不是。
     */
    public static boolean isMultipart(HttpServletRequest request) {
        return StrUtil.startWithIgnoreCase(request.getContentType(), "multipart/");
    }

    /**
     * 检查 HTTP 请求是否为 {@code application/x-www-form-urlencoded} 格式（标准表单提交）
     *
     * @param request 请求对象
     * @return {@code true} 表示表单请求，{@code false} 表示不是。
     * @see MediaType#APPLICATION_FORM_URLENCODED_VALUE
     */
    public static boolean isForm(HttpServletRequest request) {
        return StrUtil.contains(request.getContentType(), MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    /**
     * 检查 HTTP 响应是否为 {@code Server-Sent Events (SSE)} 流格式
     *
     * @param response 响应对象
     * @return {@code true} 表示 SSE 流式响应，{@code false} 表示不是。
     * @see MediaType#TEXT_EVENT_STREAM_VALUE
     */
    public static boolean isStream(HttpServletResponse response) {
        return StrUtil.contains(response.getContentType(), MediaType.TEXT_EVENT_STREAM_VALUE);
    }
}
