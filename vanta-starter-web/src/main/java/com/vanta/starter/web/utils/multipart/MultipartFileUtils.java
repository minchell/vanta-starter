package com.vanta.starter.web.utils.multipart;

import cn.hutool.core.io.IoUtil;
import com.vanta.starter.core.exception.BaseException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * MultipartFile 转换工具类。
 * <p>
 * 该工具类可以把本地文件、字节数组或输入流包装为 Spring {@link MultipartFile}，
 * 主要服务于测试、导入流程和内部文件处理流程，不会主动上传文件到远程服务。
 * </p>
 */
public class MultipartFileUtils {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private MultipartFileUtils() {
    }

    /**
     * 把本地文件转换为 MultipartFile。
     *
     * @param file 本地文件。
     * @return Spring MultipartFile 对象。
     * @throws IOException 打开本地文件输入流失败时抛出。
     */
    public static MultipartFile toMultipartFile(File file) throws IOException {
        FileItem fileItem = createFileItem(Files.newInputStream(file.toPath()), file.getName());
        return new CommonsMultipartFile(fileItem);
    }

    /**
     * 把字节数组转换为 MultipartFile。
     *
     * @param bytes    文件字节。
     * @param fileName 文件名。
     * @return Spring MultipartFile 对象。
     */
    public static MultipartFile toMultipartFile(byte[] bytes, String fileName) {
        FileItem fileItem = createFileItem(new ByteArrayInputStream(bytes), fileName);
        return new CommonsMultipartFile(fileItem);
    }

    /**
     * 创建默认字段名的 FileItem。
     *
     * @param is       输入流。
     * @param fileName 文件名。
     * @return Commons FileUpload 文件项。
     */
    public static FileItem createFileItem(InputStream is, String fileName) {
        return createFileItem(is, "file", fileName, MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    /**
     * 创建 FileItem。
     * <p>
     * 方法会把输入流复制到 Commons FileUpload 的文件项输出流中，并在完成后关闭输入流。
     * </p>
     *
     * @param is          输入流。
     * @param fieldName   表单字段名。
     * @param fileName    文件名。
     * @param contentType 内容类型。
     * @return Commons FileUpload 文件项。
     */
    public static FileItem createFileItem(InputStream is, String fieldName, String fileName, String contentType) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        FileItem fileItem = factory.createItem(fieldName, contentType, true, fileName);
        // 拷贝流
        try (OutputStream os = fileItem.getOutputStream()) {
            IoUtil.copy(is, os);
        } catch (IOException e) {
            throw new BaseException("创建文件项失败", e);
        } finally {
            IoUtil.close(is);
        }
        return fileItem;
    }
}
