package com.vanta.starter.web.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * 文件上传下载工具类。
 * <p>
 * 该工具类提供基于 Spring {@link MultipartFile} 的本地落盘和基于 Servlet 响应的文件下载能力。
 * 上传方法会写入调用方指定的本地文件路径，不会主动接入对象存储或远程文件服务。
 * </p>
 */
public class FileUploadUtils {

    /**
     * 当前工具类日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(FileUploadUtils.class);

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private FileUploadUtils() {
    }

    /**
     * 上传文件到本地目录。
     * <p>
     * 保留原文件名时，会在原文件名前缀后追加时间戳，降低同名覆盖概率；不保留原文件名时使用 UUID 作为文件名。
     * 如果父目录不存在，会尝试自动创建；写入失败时记录日志并返回 {@code null}。
     * </p>
     *
     * @param multipartFile          源文件对象。
     * @param filePath               目标目录路径，调用方需要保证路径分隔符符合当前运行环境。
     * @param isKeepOriginalFilename 是否保留原文件名前缀。
     * @return 写入后的目标文件对象；失败时返回 {@code null}。
     */
    public static File upload(MultipartFile multipartFile, String filePath, boolean isKeepOriginalFilename) {
        String originalFilename = multipartFile.getOriginalFilename();
        String extensionName = FileNameUtil.extName(originalFilename);
        String fileName;

        if (isKeepOriginalFilename) {
            fileName = "%s-%s.%s".formatted(
                    FileNameUtil.getPrefix(originalFilename),
                    DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN),
                    extensionName
            );
        } else {
            fileName = "%s.%s".formatted(IdUtil.fastSimpleUUID(), extensionName);
        }

        try {
            String pathname = filePath + fileName;
            File dest = new File(pathname).getCanonicalFile();
            // 如果父路径不存在，自动创建
            if (!dest.getParentFile().exists() && (!dest.getParentFile().mkdirs())) {
                log.error("Create upload file parent path failed.");
            }
            // 文件写入
            multipartFile.transferTo(dest);
            return dest;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 下载本地文件。
     *
     * @param response HTTP 响应对象。
     * @param file     需要下载的本地文件。
     * @throws IOException 打开文件或写出响应失败时抛出。
     */
    public static void download(HttpServletResponse response, File file) throws IOException {
        download(response, new FileInputStream(file), file.getName());
    }

    /**
     * 下载输入流内容。
     * <p>
     * 方法会设置 UTF-8 编码、二进制响应类型和附件下载响应头，并在写出完成后关闭输入流和响应输出流。
     * </p>
     *
     * @param response    HTTP 响应对象。
     * @param inputStream 待下载内容输入流。
     * @param fileName    下载时展示给客户端的文件名。
     * @throws IOException 读取输入流或写出响应失败时抛出。
     */
    public static void download(HttpServletResponse response, InputStream inputStream, String fileName) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLUtil.encode(fileName));

        try (inputStream; var outputStream = response.getOutputStream()) {
            response.setContentLengthLong(inputStream.transferTo(outputStream));
        }
    }
}
