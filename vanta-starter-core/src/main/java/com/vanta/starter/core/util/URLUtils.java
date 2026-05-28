package com.vanta.starter.core.util;

import cn.hutool.http.HttpUtil;

/**
 * URL 判断工具类。
 * <p>
 * 该工具类用于识别字符串是否为 HTTP 或 HTTPS 地址，只做本地字符串规则判断，不会发起网络请求。
 * </p>
 */
public class URLUtils {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private URLUtils() {
    }

    /**
     * 提供的 URL 是否为 HTTP URL（协议包括："http"，"https"）
     *
     * @param url URL
     * @return 是否为 HTTP URL
     */
    public static boolean isHttpUrl(String url) {
        return HttpUtil.isHttp(url) || HttpUtil.isHttps(url);
    }
}
