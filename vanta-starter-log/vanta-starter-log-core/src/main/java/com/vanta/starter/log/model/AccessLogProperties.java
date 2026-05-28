package com.vanta.starter.log.model;

import java.util.ArrayList;
import java.util.List;


/**
 * 访问日志打印配置。
 *
 * <p>该配置用于控制是否在访问日志中打印请求参数、是否截断超长参数、是否脱敏敏感参数。
 * 它只影响访问日志输出，不改变请求对象、响应对象或业务处理结果。</p>
 */
public class AccessLogProperties {

    /**
     * 是否启用
     * <p>
     * 不记录请求日志也支持开启打印访问日志
     * </p>
     */
    private boolean enabled = false;

    /**
     * 是否打印请求参数（body/query/form）
     * <p>开启后，访问日志会打印请求参数</p>
     */
    private boolean isPrintRequestParam = false;

    /**
     * 是否自动截断超长参数值（如 base64、大文本）
     * <p>开启后，超过指定长度的参数值将会自动截断处理</p>
     */
    private boolean longParamTruncate = false;

    /**
     * 超长参数检测阈值（单位：字符）
     * <p>当参数值长度超过此值时，触发截断规则</p>
     * <p>默认：2000，仅在 {@link #longParamTruncate} 启用时生效</p>
     */
    private int longParamThreshold = 2000;

    /**
     * 超长参数最大保留长度（单位：字符）
     * <p>当参数超过 {@link #longParamThreshold} 时，强制截断到此长度</p>
     * <p>默认：50，仅在 {@link #longParamTruncate} 启用时生效</p>
     */
    private int longParamMaxLength = 50;

    /**
     * 截断后追加的后缀符号（如配置 "..." 会让截断内容更直观）
     * <p>建议配置 3-5 个非占宽字符，默认为 ...</p>
     * <p>仅在 {@link #longParamTruncate} 启用时生效</p>
     */
    private String longParamSuffix = "...";

    /**
     * 是否过滤敏感参数
     * <p>开启后会对敏感参数进行过滤，默认不过滤</p>
     */
    private boolean isParamSensitive = false;

    /**
     * 敏感参数字段列表（如：password,token,idCard）
     * <p>支持精确匹配（区分大小写）</p>
     * <p>示例值：password,oldPassword</p>
     */
    private List<String> sensitiveParams = new ArrayList<>();

    /**
     * 读取访问日志打印开关。
     *
     * @return true 表示打印访问日志，false 表示不打印访问日志
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置访问日志打印开关。
     *
     * @param enabled true 表示打印访问日志，false 表示不打印访问日志
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 读取是否打印请求参数。
     *
     * @return true 表示打印 query、form 或 body 参数，false 表示不打印请求参数
     */
    public boolean isPrintRequestParam() {
        return isPrintRequestParam;
    }

    /**
     * 设置是否打印请求参数。
     *
     * @param printRequestParam true 表示打印请求参数，false 表示不打印请求参数
     */
    public void setPrintRequestParam(boolean printRequestParam) {
        isPrintRequestParam = printRequestParam;
    }

    /**
     * 读取超长参数截断开关。
     *
     * @return true 表示启用截断，false 表示保留完整参数值
     */
    public boolean isLongParamTruncate() {
        return longParamTruncate;
    }

    /**
     * 设置超长参数截断开关。
     *
     * @param longParamTruncate true 表示启用截断，false 表示保留完整参数值
     */
    public void setLongParamTruncate(boolean longParamTruncate) {
        this.longParamTruncate = longParamTruncate;
    }

    /**
     * 读取超长参数判定阈值。
     *
     * @return 参数长度超过该值时触发截断逻辑
     */
    public int getLongParamThreshold() {
        return longParamThreshold;
    }

    /**
     * 设置超长参数判定阈值。
     *
     * @param longParamThreshold 参数长度超过该值时触发截断逻辑
     */
    public void setLongParamThreshold(int longParamThreshold) {
        this.longParamThreshold = longParamThreshold;
    }

    /**
     * 读取超长参数截断后保留长度。
     *
     * @return 截断后最多保留的字符数
     */
    public int getLongParamMaxLength() {
        return longParamMaxLength;
    }

    /**
     * 设置超长参数截断后保留长度。
     *
     * @param longParamMaxLength 截断后最多保留的字符数
     */
    public void setLongParamMaxLength(int longParamMaxLength) {
        this.longParamMaxLength = longParamMaxLength;
    }

    /**
     * 读取超长参数截断后缀。
     *
     * @return 追加在截断值末尾的后缀
     */
    public String getLongParamSuffix() {
        return longParamSuffix;
    }

    /**
     * 设置超长参数截断后缀。
     *
     * @param longParamSuffix 追加在截断值末尾的后缀
     */
    public void setLongParamSuffix(String longParamSuffix) {
        this.longParamSuffix = longParamSuffix;
    }

    /**
     * 读取敏感参数过滤开关。
     *
     * @return true 表示命中敏感字段的参数会被过滤，false 表示不做敏感参数过滤
     */
    public boolean isParamSensitive() {
        return isParamSensitive;
    }

    /**
     * 设置敏感参数过滤开关。
     *
     * @param paramSensitive true 表示过滤敏感参数，false 表示不做敏感参数过滤
     */
    public void setParamSensitive(boolean paramSensitive) {
        isParamSensitive = paramSensitive;
    }

    /**
     * 读取敏感参数字段名列表。
     *
     * @return 需要过滤的敏感参数字段名列表
     */
    public List<String> getSensitiveParams() {
        return sensitiveParams;
    }

    /**
     * 设置敏感参数字段名列表。
     *
     * @param sensitiveParams 需要过滤的敏感参数字段名列表
     */
    public void setSensitiveParams(List<String> sensitiveParams) {
        this.sensitiveParams = sensitiveParams;
    }
}
