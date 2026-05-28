package com.vanta.starter.web.utils.multipart;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 基于 Apache Commons FileUpload 的 {@link MultipartFile} 适配器。
 * <p>
 * 该类把 Commons FileUpload 的 {@link FileItem} 包装为 Spring Web 可识别的 {@link MultipartFile}，
 * 便于工具代码把本地文件、字节数组或输入流转换为标准 multipart 文件对象。
 * 该实现只操作本地内存或临时文件，不会主动上传到远程存储。
 * </p>
 */
public class CommonsMultipartFile implements MultipartFile, Serializable {

    /**
     * Commons Multipart 文件适配器日志记录器。
     */
    protected static final Log logger = LogFactory.getLog(CommonsMultipartFile.class);

    /**
     * 被包装的 Commons FileUpload 文件项。
     */
    private final FileItem fileItem;

    /**
     * 文件项创建时记录的文件大小。
     */
    private final long size;

    /**
     * 是否保留客户端传入的原始文件名。
     * <p>
     * 默认不保留路径信息，防止浏览器或客户端把本地路径片段带入服务端文件名。
     * </p>
     */
    private boolean preserveFilename = false;

    /**
     * 创建包装指定 {@link FileItem} 的 multipart 文件。
     *
     * @param fileItem 需要包装的文件项。
     */
    public CommonsMultipartFile(FileItem fileItem) {
        this.fileItem = fileItem;
        this.size = this.fileItem.getSize();
    }

    /**
     * 获取底层 {@link FileItem}。
     *
     * @return 底层 Commons FileUpload 文件项。
     */
    public final FileItem getFileItem() {
        return this.fileItem;
    }

    /**
     * 设置是否保留客户端传入的完整文件名。
     * <p>
     * 默认值为 {@code false}，会移除可能包含的路径前缀，只保留实际文件名。
     * 如果设置为 {@code true}，{@link #getOriginalFilename()} 会原样返回客户端传入文件名，包括潜在路径分隔符。
     * </p>
     *
     * @param preserveFilename 是否保留完整文件名。
     * @see #getOriginalFilename()
     */
    public void setPreserveFilename(boolean preserveFilename) {
        this.preserveFilename = preserveFilename;
    }

    /**
     * 获取表单字段名。
     *
     * @return 表单字段名。
     */
    @Override
    public String getName() {
        return this.fileItem.getFieldName();
    }

    /**
     * 获取原始文件名。
     * <p>
     * 当未启用完整文件名保留时，会自动移除 Unix 或 Windows 路径分隔符之前的内容。
     * </p>
     *
     * @return 原始文件名；底层文件名为空时返回空字符串。
     */
    @Override
    public String getOriginalFilename() {
        String filename = this.fileItem.getName();
        if (filename == null) {
            // Should never happen.
            return "";
        }
        if (this.preserveFilename) {
            // Do not try to strip off a path...
            return filename;
        }

        // Check for Unix-style path
        int unixSep = filename.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = filename.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = Math.max(winSep, unixSep);
        if (pos != -1) {
            // Any sort of path separator found...
            return filename.substring(pos + 1);
        } else {
            // A plain name
            return filename;
        }
    }

    /**
     * 获取文件内容类型。
     *
     * @return 文件内容类型。
     */
    @Override
    public String getContentType() {
        return this.fileItem.getContentType();
    }

    /**
     * 判断文件是否为空。
     *
     * @return 文件大小为 0 时返回 {@code true}。
     */
    @Override
    public boolean isEmpty() {
        return (this.size == 0);
    }

    /**
     * 获取文件大小。
     *
     * @return 文件大小，单位字节。
     */
    @Override
    public long getSize() {
        return this.size;
    }

    /**
     * 获取文件内容字节数组。
     *
     * @return 文件内容字节数组；底层返回空时使用空数组。
     */
    @Override
    public byte[] getBytes() {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        byte[] bytes = this.fileItem.get();
        return (bytes != null ? bytes : new byte[0]);
    }

    /**
     * 获取文件内容输入流。
     *
     * @return 文件内容输入流；底层返回空时使用空输入流。
     * @throws IOException 打开输入流失败时抛出。
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        InputStream inputStream = this.fileItem.getInputStream();
        return (inputStream != null ? inputStream : InputStream.nullInputStream());
    }

    /**
     * 把文件内容转存到目标文件。
     *
     * @param dest 目标文件。
     * @throws IOException           写入目标文件失败时抛出。
     * @throws IllegalStateException 临时文件已经被移动或不可再次读取时抛出。
     */
    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again");
        }

        if (dest.exists() && !dest.delete()) {
            throw new IOException("Destination file [" + dest
                    .getAbsolutePath() + "] already exists and could not be deleted");
        }

        try {
            this.fileItem.write(dest);
            LogFormatUtils.traceDebug(logger, traceOn -> {
                String action = "transferred";
                if (!this.fileItem.isInMemory()) {
                    action = (isAvailable() ? "copied" : "moved");
                }
                return "Part '" + getName() + "',  filename '" + getOriginalFilename() + "'" + (Boolean.TRUE
                        .equals(traceOn) ? ", stored " + getStorageDescription() : "") + ": " + action + " to [" + dest
                        .getAbsolutePath() + "]";
            });
        } catch (FileUploadException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        } catch (IllegalStateException | IOException ex) {
            // Pass through IllegalStateException when coming from FileItem directly,
            // or propagate an exception from I/O operations within FileItem.write
            throw ex;
        } catch (Exception ex) {
            throw new IOException("File transfer failed", ex);
        }
    }

    /**
     * 把文件内容转存到目标路径。
     *
     * @param dest 目标路径。
     * @throws IOException           写入目标路径失败时抛出。
     * @throws IllegalStateException 临时文件已经被移动或不可再次读取时抛出。
     */
    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again");
        }

        FileCopyUtils.copy(this.fileItem.getInputStream(), Files.newOutputStream(dest));
    }

    /**
     * 判断 multipart 内容是否仍可读取。
     * <p>
     * 如果底层临时文件已经被移动，内容就不能再次读取；内存文件项始终可读取。
     * </p>
     *
     * @return 内容仍可读取时返回 {@code true}。
     */
    protected boolean isAvailable() {
        // If in memory, it's available.
        if (this.fileItem.isInMemory()) {
            return true;
        }
        // Check actual existence of temporary file.
        if (this.fileItem instanceof DiskFileItem df) {
            return df.getStoreLocation().exists();
        }
        // Check whether current file size is different than original one.
        return (this.fileItem.getSize() == this.size);
    }

    /**
     * 获取 multipart 内容存储位置描述。
     *
     * @return 存储位置描述，例如内存、具体临时文件路径或磁盘。
     */
    public String getStorageDescription() {
        if (this.fileItem.isInMemory()) {
            return "in memory";
        } else if (this.fileItem instanceof DiskFileItem df) {
            return "at [" + df.getStoreLocation().getAbsolutePath() + "]";
        } else {
            return "on disk";
        }
    }

    /**
     * 获取 multipart 文件的调试字符串。
     *
     * @return 包含字段名、文件名、内容类型和大小的字符串。
     */
    @Override
    public String toString() {
        return "MultipartFile[field=\"" + this.fileItem.getFieldName() + "\"" + (this.fileItem.getName() != null
                ? ", filename=" + this.fileItem.getName()
                : "") + (this.fileItem.getContentType() != null
                ? ", contentType=" + this.fileItem.getContentType()
                : "") + ", size=" + this.fileItem.getSize() + "]";
    }
}
