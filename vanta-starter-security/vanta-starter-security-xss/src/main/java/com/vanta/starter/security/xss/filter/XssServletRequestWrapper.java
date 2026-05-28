package com.vanta.starter.security.xss.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.Method;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.security.xss.autoconfigure.XssProperties;
import com.vanta.starter.security.xss.enums.XssMode;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * 针对 XssServletRequest 进行过滤的包装类
 */
public class XssServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * xssProperties 字段。
     * <p>用于保存 安全防护能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final XssProperties xssProperties;

    /**
     * body 字段。
     * <p>用于保存 安全防护能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private String body = "";

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param request       request 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @param xssProperties xssProperties 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    public XssServletRequestWrapper(HttpServletRequest request, XssProperties xssProperties) throws IOException {
        super(request);
        this.xssProperties = xssProperties;
        if (CharSequenceUtil.equalsAnyIgnoreCase(request.getMethod().toUpperCase(), Method.POST.name(), Method.PATCH
                .name(), Method.PUT.name())) {
            String charset = StrUtil.blankToDefault(request.getCharacterEncoding(), CharsetUtil.UTF_8);
            body = IoUtil.read(request.getInputStream(), CharsetUtil.charset(charset));
            if (CharSequenceUtil.isBlank(body)) {
                return;
            }
            body = this.handleTag(body);
        }
    }

    static ServletInputStream getServletInputStream(String body) {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            /**
             * 执行 read 逻辑。
             * 该方法属于 安全防护能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
             * @return 方法执行后的结果对象、配置值或运行时依赖
             */
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            /**
             * 读取 Finished 配置或状态。
             * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
             * @return 方法执行后的结果对象、配置值或运行时依赖
             */
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            /**
             * 读取 Ready 配置或状态。
             * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
             * @return 方法执行后的结果对象、配置值或运行时依赖
             */
            @Override
            public boolean isReady() {
                return false;
            }

            /**
             * 设置 Read Listener 配置值。
             * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
             * @param readListener readListener 参数，调用方应传入与 安全防护能力 场景匹配的有效值
             */
            @Override
            public void setReadListener(ReadListener readListener) {
                // 设置监听器
            }
        };
    }

    /**
     * 读取 Reader 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public BufferedReader getReader() {
        return IoUtil.toBuffered(new StringReader(body));
    }

    /**
     * 读取 Input Stream 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public ServletInputStream getInputStream() {
        return getServletInputStream(body);
    }

    /**
     * 读取 Query String 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String getQueryString() {
        return this.handleTag(super.getQueryString());
    }

    /**
     * 读取 Parameter 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @param name name 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String getParameter(String name) {
        return this.handleTag(super.getParameter(name));
    }

    /**
     * 读取 Parameter Values 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @param name name 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (ArrayUtil.isEmpty(values)) {
            return values;
        }
        int length = values.length;
        String[] resultValues = new String[length];
        for (int i = 0; i < length; i++) {
            resultValues[i] = this.handleTag(values[i]);
        }
        return resultValues;
    }

    /**
     * 对文本内容进行 XSS 处理
     *
     * @param content 文本内容
     * @return 返回处理过后内容
     */
    private String handleTag(String content) {
        if (CharSequenceUtil.isBlank(content)) {
            return content;
        }
        XssMode mode = xssProperties.getMode();
        // 转义
        if (XssMode.ESCAPE.equals(mode)) {
            List<String> reStr = ReUtil.findAllGroup0(HtmlUtil.RE_HTML_MARK, content);
            if (CollUtil.isEmpty(reStr)) {
                return content;
            }
            for (String s : reStr) {
                content = content.replace(s, EscapeUtil.escapeHtml4(s)
                        .replace(StringConstants.BACKSLASH, StringConstants.EMPTY));
            }
            return content;
        }
        // 清理
        return HtmlUtil.cleanHtmlTag(content);
    }

}
