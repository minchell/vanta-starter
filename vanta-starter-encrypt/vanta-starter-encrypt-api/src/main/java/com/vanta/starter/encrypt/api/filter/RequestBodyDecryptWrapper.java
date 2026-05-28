package com.vanta.starter.encrypt.api.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import com.vanta.starter.encrypt.util.EncryptUtils;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 请求体解密包装类
 */
public class RequestBodyDecryptWrapper extends HttpServletRequestWrapper {

    /**
     * body 字段。
     * <p>用于保存 加密能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final byte[] body;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param request         request 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param privateKey      privateKey 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param secretKeyHeader secretKeyHeader 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @throws IOException 当底层客户端、配置解析或远程调用失败时抛出
     */
    public RequestBodyDecryptWrapper(HttpServletRequest request, String privateKey, String secretKeyHeader) throws IOException {
        super(request);
        this.body = getDecryptContent(request, privateKey, secretKeyHeader);
    }

    /**
     * 获取解密后的请求体
     *
     * @param request         请求对象
     * @param privateKey      RSA私钥
     * @param secretKeyHeader 密钥头
     * @return 解密后的请求体
     * @throws IOException /
     */
    public byte[] getDecryptContent(HttpServletRequest request, String privateKey, String secretKeyHeader) throws IOException {
        // 通过 请求头 获取 AES 密钥，密钥内容经过 RSA 加密
        String secretKeyByRsa = request.getHeader(secretKeyHeader);
        // 通过 RSA 解密，获取 AES 密钥，密钥内容经过 Base64 编码
        String secretKeyByBase64 = EncryptUtils.decryptByRsa(secretKeyByRsa, privateKey);
        // 通过 Base64 解码，获取 AES 密钥
        String aesSecretKey = EncryptUtils.decodeByBase64(secretKeyByBase64);
        request.setCharacterEncoding(CharsetUtil.UTF_8);
        byte[] readBytes = IoUtil.readBytes(request.getInputStream(), false);
        String requestBody = new String(readBytes, StandardCharsets.UTF_8);
        // 通过 AES 密钥，解密 请求体
        return EncryptUtils.decryptByAes(requestBody, aesSecretKey).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 读取 Reader 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * 读取 Content Length 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public int getContentLength() {
        return body.length;
    }

    /**
     * 读取 Content Length Long 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    /**
     * 读取 Content Type 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String getContentType() {
        return MediaType.APPLICATION_JSON_VALUE;
    }

    /**
     * 读取 Input Stream 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream stream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            /**
             * 执行 read 逻辑。
             * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
             * @return 方法执行后的结果对象、配置值或运行时依赖
             */
            @Override
            public int read() {
                return stream.read();
            }

            /**
             * 执行 available 逻辑。
             * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
             * @return 方法执行后的结果对象、配置值或运行时依赖
             */
            @Override
            public int available() {
                return body.length;
            }

            /**
             * 读取 Finished 配置或状态。
             * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
             * @return 方法执行后的结果对象、配置值或运行时依赖
             */
            @Override
            public boolean isFinished() {
                return false;
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
             * @param readListener readListener 参数，调用方应传入与 加密能力 场景匹配的有效值
             */
            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
