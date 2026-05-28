package com.vanta.starter.web.wrapper;

import com.vanta.starter.web.utils.ServletUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 支持响应体重复读取的 {@link HttpServletResponse} 包装器。
 * <p>
 * 该包装器把普通响应内容先写入内存缓存，便于日志、加密、统一响应包装等过滤器在真正写回客户端前读取响应体。
 * 对 SSE 等流式响应会直接透传原始输出流，避免缓存导致流式推送被阻塞或一次性写出。
 * </p>
 */
public class RepeatReadResponseWrapper extends HttpServletResponseWrapper {

    /**
     * 普通响应内容的内存缓存。
     */
    private final ByteArrayOutputStream cachedOutputStream = new ByteArrayOutputStream();

    /**
     * 写入缓存输出流的字符写入器。
     */
    private final PrintWriter writer = new PrintWriter(cachedOutputStream, true);

    /**
     * 是否为流式响应
     */
    private boolean isStreamingResponse = false;

    /**
     * 创建可重复读取的响应包装器。
     *
     * @param response 原始 HTTP 响应。
     */
    public RepeatReadResponseWrapper(HttpServletResponse response) {
        super(response);
        isStreamingResponse = ServletUtils.isStream(response);
    }

    /**
     * 获取响应输出流。
     * <p>
     * 普通响应会返回写入缓存的输出流；流式响应直接返回原始响应输出流，确保实时推送语义不被破坏。
     * </p>
     *
     * @return 响应输出流。
     * @throws IOException 获取原始输出流失败时抛出。
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // 对于 SSE 流式响应，直接返回原始响应流，不做额外处理
        if (isStreamingResponse) {
            return super.getOutputStream();
        }

        return new ServletOutputStream() {
            /**
             * 判断当前输出流是否可以写入。
             *
             * @return 内存缓存始终可写，固定返回 {@code true}。
             */
            @Override
            public boolean isReady() {
                return true;
            }

            /**
             * 设置 Servlet 非阻塞写入监听器。
             * <p>
             * 当前缓存输出流不需要异步写入监听，因此该方法保持空实现。
             * </p>
             *
             * @param writeListener Servlet 写入监听器。
             */
            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            /**
             * 写入单个字节到响应缓存。
             *
             * @param b 需要写入的字节。
             * @throws IOException 写入缓存失败时抛出。
             */
            @Override
            public void write(int b) throws IOException {
                cachedOutputStream.write(b);
            }

            /**
             * 写入字节数组到响应缓存。
             *
             * @param b 需要写入的字节数组。
             * @throws IOException 写入缓存失败时抛出。
             */
            @Override
            public void write(byte[] b) throws IOException {
                cachedOutputStream.write(b);
            }

            /**
             * 写入字节数组指定区间到响应缓存。
             *
             * @param b   需要写入的字节数组。
             * @param off 起始偏移量。
             * @param len 写入长度。
             * @throws IOException 写入缓存失败时抛出。
             */
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                cachedOutputStream.write(b, off, len);
            }
        };
    }

    /**
     * 获取响应字符写入器。
     * <p>
     * 普通响应返回缓存写入器；流式响应直接返回原始响应写入器，避免破坏流式传输。
     * </p>
     *
     * @return 响应字符写入器。
     * @throws IOException 获取原始写入器失败时抛出。
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        if (isStreamingResponse) {
            // 对于 SSE 流式响应，直接返回原始响应写入器，不做额外处理
            return super.getWriter();
        }
        return writer;
    }

    /**
     * 获取缓存的响应内容
     *
     * @return 缓存的响应内容
     */
    public String getResponseContent() {
        if (!isStreamingResponse) {
            writer.flush();
            return cachedOutputStream.toString(StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 将缓存的响应内容复制到原始响应中
     *
     * @throws IOException IO 异常
     */
    public void copyBodyToResponse() throws IOException {
        if (!isStreamingResponse && cachedOutputStream.size() > 0) {
            getResponse().getOutputStream().write(cachedOutputStream.toByteArray());
        }
    }

    /**
     * 是否为流式响应
     *
     * @return 是否为流式响应
     */
    public boolean isStreamingResponse() {
        return isStreamingResponse;
    }
}
