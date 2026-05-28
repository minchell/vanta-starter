package com.vanta.starter.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HtmlUtil;
import com.vanta.starter.core.constant.StringConstants;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;

import java.util.Objects;
import java.util.Set;

/**
 * IP 地址辅助工具类。
 * <p>
 * 该工具类提供内网 IPv4 判断和基于 ip2region 本地库的 IPv4 归属地解析。
 * 归属地解析依赖 Spring 容器中存在 {@link Ip2regionSearcher} Bean，但查询过程使用本地内存库，不主动访问公网接口。
 * </p>
 */
public class IpUtils {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private IpUtils() {
    }

    /**
     * 查询 IPv4 归属地。
     * <p>
     * 内网地址直接返回“内网IP”；公网地址通过 ip2region 本地库解析国家、区域、省、市和运营商，并使用竖线拼接。
     * </p>
     *
     * @param ip IPv4 地址。
     * @return IP 归属地；无法解析时返回 {@code null}。
     */
    public static String getIpv4Address(String ip) {
        if (isInnerIpv4(ip)) {
            return "内网IP";
        }

        Ip2regionSearcher ip2regionSearcher = SpringUtil.getBean(Ip2regionSearcher.class);
        IpInfo ipInfo = ip2regionSearcher.memorySearch(ip);
        if (ipInfo == null) {
            return null;
        }

        Set<String> regionSet = CollUtil.newLinkedHashSet(ipInfo.getCountry(), ipInfo.getRegion(), ipInfo
                .getProvince(), ipInfo.getCity(), ipInfo.getIsp());
        regionSet.removeIf(Objects::isNull);
        return String.join(StringConstants.PIPE, regionSet);
    }

    /**
     * 判断是否为内网 IPv4。
     * <p>
     * 方法会兼容 IPv6 本机回环地址 {@code 0:0:0:0:0:0:0:1}，并先清理 HTML 标签再交给网络工具判断。
     * </p>
     *
     * @param ip IP 地址。
     * @return {@code true} 表示内网地址，{@code false} 表示非内网地址。
     */
    public static boolean isInnerIpv4(String ip) {
        return NetUtil.isInnerIP("0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : HtmlUtil.cleanHtmlTag(ip));
    }
}
