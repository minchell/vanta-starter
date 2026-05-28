package com.vanta.starter.core.autoconfigure.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 应用基础信息配置属性。
 * <p>
 * 该配置绑定 {@code application.*}，用于描述当前接入项目的名称、版本、联系人、许可协议等元信息。
 * 它不主动创建远程连接，也不要求业务项目必须提供全部字段，未配置的字段会保持 {@code null} 或默认值。
 * </p>
 */
@ConfigurationProperties("application")
public class ApplicationProperties {

    /**
     * 应用唯一标识ID
     */
    private String id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用版本号
     */
    private String version;

    /**
     * 应用访问地址或官网地址URL
     */
    private String url;

    /**
     * 应用基础包名
     */
    private String basePackage;

    /**
     * 联系人配置
     */
    private Contact contact;

    /**
     * 许可协议配置
     */
    private License license;

    /**
     * 是否为生产环境
     * <p>{@code true} 表示生产环境，{@code false} 表示非生产环境</p>
     */
    private boolean production = false;

    /**
     * 获取应用唯一标识ID。
     *
     * @return 应用唯一标识ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置应用唯一标识ID。
     *
     * @param id 应用唯一标识ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取应用名称。
     *
     * @return 应用名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置应用名称。
     *
     * @param name 应用名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取应用描述。
     *
     * @return 应用描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置应用描述。
     *
     * @param description 应用描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取应用版本号。
     *
     * @return 应用版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置应用版本号。
     *
     * @param version 应用版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取应用访问地址或官网地址URL。
     *
     * @return 应用访问地址或官网地址URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置应用访问地址或官网地址URL。
     *
     * @param url 应用访问地址或官网地址URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取应用基础包名。
     *
     * @return 应用基础包名
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * 设置应用基础包名。
     *
     * @param basePackage 应用基础包名
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * 获取联系人配置。
     *
     * @return 联系人配置
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * 设置联系人配置。
     *
     * @param contact 联系人配置
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * 获取许可协议配置。
     *
     * @return 许可协议配置
     */
    public License getLicense() {
        return license;
    }

    /**
     * 设置许可协议配置。
     *
     * @param license 许可协议配置
     */
    public void setLicense(License license) {
        this.license = license;
    }

    /**
     * 获取是否为生产环境。
     *
     * @return 是否为生产环境
     */
    public boolean isProduction() {
        return production;
    }

    /**
     * 设置是否为生产环境。
     *
     * @param production 是否为生产环境
     */
    public void setProduction(boolean production) {
        this.production = production;
    }

    /**
     * 联系人配置属性
     */
    public static class Contact {
        /**
         * 联系人名称
         */
        private String name;

        /**
         * 联系人邮箱
         */
        private String email;

        /**
         * 联系人链接地址URL
         */
        private String url;

        /**
         * 获取联系人名称。
         *
         * @return 联系人名称
         */
        public String getName() {
            return name;
        }

        /**
         * 设置联系人名称。
         *
         * @param name 联系人名称
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取联系人邮箱。
         *
         * @return 联系人邮箱
         */
        public String getEmail() {
            return email;
        }

        /**
         * 设置联系人邮箱。
         *
         * @param email 联系人邮箱
         */
        public void setEmail(String email) {
            this.email = email;
        }

        /**
         * 获取联系人链接地址URL。
         *
         * @return 联系人链接地址URL
         */
        public String getUrl() {
            return url;
        }

        /**
         * 设置联系人链接地址URL。
         *
         * @param url 联系人链接地址URL
         */
        public void setUrl(String url) {
            this.url = url;
        }
    }

    /**
     * 许可协议配置属性
     */
    public static class License {
        /**
         * 许可协议名称
         */
        private String name;

        /**
         * 许可协议链接地址URL
         */
        private String url;

        /**
         * 获取许可协议名称。
         *
         * @return 许可协议名称
         */
        public String getName() {
            return name;
        }

        /**
         * 设置许可协议名称。
         *
         * @param name 许可协议名称
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取许可协议链接地址URL。
         *
         * @return 许可协议链接地址URL
         */
        public String getUrl() {
            return url;
        }

        /**
         * 设置许可协议链接地址URL。
         *
         * @param url 许可协议链接地址URL
         */
        public void setUrl(String url) {
            this.url = url;
        }
    }
}
