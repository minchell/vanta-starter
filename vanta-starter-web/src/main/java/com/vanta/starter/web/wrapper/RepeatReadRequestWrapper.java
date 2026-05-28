package com.vanta.starter.web.wrapper;

import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.web.utils.ServletUtils;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 支持请求体重复读取的 {@link HttpServletRequest} 包装器。
 * <p>
 * Servlet 原始请求体默认只能读取一次，过滤器、拦截器和控制器如果都需要访问 body，就会互相影响。
 * 该包装器在构造时把普通请求体或表单参数缓存到内存中，后续每次调用 {@link #getInputStream()} 或 {@link #getReader()} 都从缓存重新读取。
 * 文件上传请求不会被缓存，避免大文件进入内存并破坏 multipart 解析流程。
 * </p>
 */
public class RepeatReadRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存内容
     */
    private final FastByteArrayOutputStream cachedContent;
    /**
     * 字符编码
     */
    private final String characterEncoding;
    /**
     * 基于缓存内容创建的输入流。
     * <p>
     * 非 multipart 请求会初始化该字段；multipart 请求保留原始输入流，因此该字段不会被使用。
     * </p>
     */
    private ContentCachingInputStream contentCachingInputStream;

    /**
     * 创建支持重复读取的请求包装器。
     * <p>
     * 构造阶段会确定字符集、初始化缓存区，并根据请求类型决定是否缓存请求内容。
     * 普通 body 请求直接复制输入流，表单请求会把参数重新编码为标准表单内容，文件上传请求则跳过缓存。
     * </p>
     *
     * @param request 原始 HTTP 请求。
     * @throws IOException 读取原始请求体失败时抛出。
     */
    public RepeatReadRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.characterEncoding = request.getCharacterEncoding() != null
                ? request.getCharacterEncoding()
                : StandardCharsets.UTF_8.name();
        int contentLength = super.getRequest().getContentLength();
        cachedContent = (contentLength > 0)
                ? new FastByteArrayOutputStream(contentLength)
                : new FastByteArrayOutputStream();
        // 判断是否为文件上传请求
        if (!ServletUtils.isMultipart(request)) {
            if (ServletUtils.isForm(request)) {
                writeRequestParametersToCachedContent();
            } else {
                StreamUtils.copy(request.getInputStream(), cachedContent);
            }
            contentCachingInputStream = new ContentCachingInputStream(cachedContent.toByteArray());
        }
    }

    /**
     * 获取可重复读取的请求输入流。
     * <p>
     * 非 multipart 请求每次调用前都会重置缓存流；multipart 请求直接返回原始输入流，避免破坏文件上传处理。
     * </p>
     *
     * @return 请求输入流。
     * @throws IOException 获取原始请求输入流或重置缓存流失败时抛出。
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 如果是文件上传，直接返回原始输入流
        if (ServletUtils.isMultipart((HttpServletRequest) super.getRequest())) {
            return super.getRequest().getInputStream();
        }
        synchronized (this) {
            contentCachingInputStream.reset();
            return contentCachingInputStream;
        }
    }

    /**
     * 获取可重复读取的请求字符读取器。
     * <p>
     * {@link BufferedReader} 本身不适合被反复 reset，因此每次调用都会基于当前缓存输入流重新创建读取器。
     * multipart 请求直接返回原始读取器，避免影响文件上传。
     * </p>
     *
     * @return 请求字符读取器。
     * @throws IOException 获取输入流或读取器失败时抛出。
     */
    @Override
    public BufferedReader getReader() throws IOException {
        // 如果是文件上传，直接返回原始Reader
        if (ServletUtils.isMultipart((HttpServletRequest) super.getRequest())) {
            return super.getRequest().getReader();
        }

        // BufferedReader不支持多次reset()（除非手动调用 mark() 并控制其生命周期），最安全的方式是每次调用getReader()时基于缓存内容重新创建一个新的BufferedReader实例。
        synchronized (this) {
            return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
        }
    }

    /**
     * 把表单参数写入请求体缓存。
     * <p>
     * 表单请求在 Servlet 容器读取参数后，原始输入流可能已经被消费；这里按照
     * {@code application/x-www-form-urlencoded} 规则重新编码参数，保证后续仍能读取到等价内容。
     * </p>
     */
    private void writeRequestParametersToCachedContent() {
        try {
            if (this.cachedContent.size() == 0) {
                Map<String, String[]> form = super.getParameterMap();
                for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
                        String value = valueIterator.next();
                        this.cachedContent.write(URLEncoder.encode(name, characterEncoding).getBytes());
                        if (value != null) {
                            this.cachedContent.write(StringConstants.EQUALS.getBytes());
                            this.cachedContent.write(URLEncoder.encode(value, characterEncoding).getBytes());
                            if (valueIterator.hasNext()) {
                                this.cachedContent.write(StringConstants.AMP.getBytes());
                            }
                        }
                    }
                    if (nameIterator.hasNext()) {
                        this.cachedContent.write(StringConstants.AMP.getBytes());
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }

    /**
     * 获取请求字符编码。
     *
     * @return 请求字符编码；原始请求未指定时返回 UTF-8。
     */
    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    /**
     * 获取缓存请求体的字符串形式。
     *
     * @return 使用当前请求字符编码解码后的请求内容。
     */
    public String getContentAsString() {
        return this.cachedContent.toString(Charset.forName(getCharacterEncoding()));
    }

    /**
     * 获取原始缓存字节内容。
     *
     * @return 请求体缓存输出流，调用方可以读取其中的字节数组。
     */
    public FastByteArrayOutputStream getCachedContent() {
        return cachedContent;
    }

    /**
     * 基于内存缓存的 {@link ServletInputStream} 实现。
     * <p>
     * 该类把缓存字节数组包装为 {@link ByteArrayInputStream}，对外保持 Servlet 输入流接口。
     * 它只服务于当前请求包装器，不直接暴露给业务代码创建。
     * </p>
     */
    private static class ContentCachingInputStream extends ServletInputStream {

        /**
         * 实际读取缓存字节的委托输入流。
         */
        private final InputStream delegate;

        /**
         * 创建缓存输入流。
         *
         * @param body 已缓存的请求体字节数组。
         */
        public ContentCachingInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        /**
         * 判断输入流是否已经读取完成。
         *
         * @return 当前实现保持兼容性，固定返回 {@code false}。
         */
        public boolean isFinished() {
            return false;
        }

        /**
         * 判断输入流是否可立即读取。
         *
         * @return 内存缓存始终可立即读取，因此固定返回 {@code true}。
         */
        public boolean isReady() {
            return true;
        }

        /**
         * 设置 Servlet 非阻塞读取监听器。
         * <p>
         * 当前包装器仅用于同步读取缓存，不支持异步监听。
         * </p>
         *
         * @param readListener Servlet 读取监听器。
         */
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        /**
         * 读取下一个字节。
         *
         * @return 下一个字节；到达末尾时返回 {@code -1}。
         * @throws IOException 读取失败时抛出。
         */
        public int read() throws IOException {
            return this.delegate.read();
        }

        /**
         * 读取字节到指定数组区间。
         *
         * @param b   目标字节数组。
         * @param off 写入起始偏移量。
         * @param len 最多读取长度。
         * @return 实际读取长度；到达末尾时返回 {@code -1}。
         * @throws IOException 读取失败时抛出。
         */
        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        /**
         * 读取字节到目标数组。
         *
         * @param b 目标字节数组。
         * @return 实际读取长度；到达末尾时返回 {@code -1}。
         * @throws IOException 读取失败时抛出。
         */
        public int read(byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        /**
         * 跳过指定数量的字节。
         *
         * @param n 希望跳过的字节数量。
         * @return 实际跳过的字节数量。
         * @throws IOException 跳过失败时抛出。
         */
        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        /**
         * 获取当前仍可无阻塞读取的字节数量。
         *
         * @return 可读取字节数量。
         * @throws IOException 查询失败时抛出。
         */
        public int available() throws IOException {
            return this.delegate.available();
        }

        /**
         * 关闭委托输入流。
         *
         * @throws IOException 关闭失败时抛出。
         */
        public void close() throws IOException {
            this.delegate.close();
        }

        /**
         * 标记当前读取位置。
         *
         * @param readlimit 标记失效前允许继续读取的最大字节数。
         */
        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        /**
         * 重置到最近一次标记位置。
         *
         * @throws IOException 重置失败时抛出。
         */
        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        /**
         * 判断是否支持标记和重置。
         *
         * @return 委托输入流是否支持标记和重置。
         */
        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }
}
