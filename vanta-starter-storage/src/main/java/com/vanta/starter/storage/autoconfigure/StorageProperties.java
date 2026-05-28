package com.vanta.starter.storage.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * Vanta 存储能力配置。
 *
 * <p>首版只提供本地文件存储，默认写入相对目录，不连接任何对象存储服务。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.STORAGE)
public class StorageProperties {

    /**
     * 是否启用 Vanta 存储模板。
     * -- GETTER --
     * 读取存储能力启用状态。
     * <p>
     * <p>
     * -- SETTER --
     * 设置存储能力启用状态。
     *
     * @return true 表示注册存储模板，false 表示不启用存储 starter
     * @param enabled true 表示启用存储模板，false 表示关闭自动配置
     */
    private boolean enabled = false;

    /**
     * 存储实现类型。首版支持 local。
     * -- GETTER --
     * 读取存储实现类型。
     * <p>
     * <p>
     * -- SETTER --
     * 设置存储实现类型。
     *
     * @return 当前存储实现类型，默认 local
     * @param type 存储实现类型；首版仅支持 local，后续可扩展 s3、minio、oss 等类型
     */
    private String type = "local";

    /**
     * 本地文件存储配置。
     * -- GETTER --
     * 读取本地文件存储配置。
     * <p>
     * <p>
     * -- SETTER --
     * 设置本地文件存储配置。
     *
     * @return 本地文件存储配置对象
     * @param local 本地文件存储配置对象；为空时调用方应避免继续访问其子属性
     */
    private Local local = new Local();

    /**
     * 获取是否启用 Vanta 存储模板。
     *
     * @return 是否启用 Vanta 存储模板
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 Vanta 存储模板。
     *
     * @param enabled 是否启用 Vanta 存储模板
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取存储实现类型。首版支持 local。
     *
     * @return 存储实现类型。首版支持 local
     */
    public String getType() {
        return type;
    }

    /**
     * 设置存储实现类型。首版支持 local。
     *
     * @param type 存储实现类型。首版支持 local
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取本地文件存储配置。
     *
     * @return 本地文件存储配置
     */
    public Local getLocal() {
        return local;
    }

    /**
     * 设置本地文件存储配置。
     *
     * @param local 本地文件存储配置
     */
    public void setLocal(Local local) {
        this.local = local;
    }

    /**
     * 本地文件存储配置项。
     *
     * <p>该配置只描述本地磁盘路径，不包含对象存储账号、密码或远程地址。</p>
     */
    public static class Local {
        /**
         * 本地存储根目录。相对路径会按应用工作目录解析。
         * -- GETTER --
         * 读取本地存储根目录。
         * <p>
         * <p>
         * -- SETTER --
         * 设置本地存储根目录。
         *
         * @return 本地存储根目录
         * @param basePath 本地存储根目录；相对路径会按应用工作目录解析
         */
        private Path basePath = Path.of("./data/storage");

        /**
         * 获取本地存储根目录。相对路径会按应用工作目录解析。
         *
         * @return 本地存储根目录。相对路径会按应用工作目录解析
         */
        public Path getBasePath() {
            return basePath;
        }

        /**
         * 设置本地存储根目录。相对路径会按应用工作目录解析。
         *
         * @param basePath 本地存储根目录。相对路径会按应用工作目录解析
         */
        public void setBasePath(Path basePath) {
            this.basePath = basePath;
        }
    }
}
