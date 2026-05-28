package com.vanta.starter.encrypt.api.filter;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.encrypt.util.EncryptUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 响应体加密包装类
 */
public class ResponseBodyEncryptWrapper extends HttpServletResponseWrapper {

    /**
     * byteArrayOutputStream 字段。
     * <p>用于保存 加密能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final ByteArrayOutputStream byteArrayOutputStream;
    /**
     * servletOutputStream 字段。
     * <p>用于保存 加密能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final ServletOutputStream servletOutputStream;
    /**
     * printWriter 字段。
     * <p>用于保存 加密能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final PrintWriter printWriter;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param response response 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    public ResponseBodyEncryptWrapper(HttpServletResponse response) throws IOException {
        super(response);
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.servletOutputStream = this.getOutputStream();
        this.printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream));
    }

    /**
     * 读取 Writer 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public PrintWriter getWriter() {
        return printWriter;
    }

    /**
     * 执行 flushBuffer 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public void flushBuffer() throws IOException {
        if (servletOutputStream != null) {
            servletOutputStream.flush();
        }
        if (printWriter != null) {
            printWriter.flush();
        }
    }

    /**
     * 执行 reset 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     */
    @Override
    public void reset() {
        byteArrayOutputStream.reset();
    }

    /**
     * 读取 Response Data 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    public byte[] getResponseData() throws IOException {
        flushBuffer();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 读取 Content 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    public String getContent() throws IOException {
        flushBuffer();
        return byteArrayOutputStream.toString();
    }

    /**
     * 获取加密内容
     *
     * @param response        响应对象
     * @param publicKey       RSA公钥
     * @param secretKeyHeader 密钥头
     * @return 加密内容
     */
    public String getEncryptContent(HttpServletResponse response, String publicKey, String secretKeyHeader) throws IOException {
        // 生成 AES 密钥
        String aesSecretKey = RandomUtil.randomString(32);
        // Base64 编码
        String secretKeyByBase64 = EncryptUtils.encodeByBase64(aesSecretKey);
        // RSA 加密
        String secretKeyByRsa = EncryptUtils.encryptByRsa(secretKeyByBase64, publicKey);
        // 设置响应头
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, secretKeyHeader);
        response.setHeader(secretKeyHeader, secretKeyByRsa);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, StringConstants.ASTERISK);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, StringConstants.ASTERISK);
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        // 通过 AES 密钥，对原始内容进行加密
        return EncryptUtils.encryptByAes(this.getContent(), aesSecretKey);
    }

    /**
     * 读取 Output Stream 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
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
             * 设置 Write Listener 配置值。
             * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
             * @param writeListener writeListener 参数，调用方应传入与 加密能力 场景匹配的有效值
             */
            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            /**
             * 执行 write 逻辑。
             * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
             * @param b b 参数，调用方应传入与 加密能力 场景匹配的有效值
             * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
             */
            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
            }

            /**
             * 执行 write 逻辑。
             * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
             * @param b b 参数，调用方应传入与 加密能力 场景匹配的有效值
             * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
             */
            @Override
            public void write(byte[] b) throws IOException {
                byteArrayOutputStream.write(b);
            }

            /**
             * 执行 write 逻辑。
             * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
             * @param b b 参数，调用方应传入与 加密能力 场景匹配的有效值
             * @param off off 参数，调用方应传入与 加密能力 场景匹配的有效值
             * @param len len 参数，调用方应传入与 加密能力 场景匹配的有效值
             * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
             */
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                byteArrayOutputStream.write(b, off, len);
            }
        };
    }

}
