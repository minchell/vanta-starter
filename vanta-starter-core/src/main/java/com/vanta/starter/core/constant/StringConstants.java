package com.vanta.starter.core.constant;

/**
 * 字符串常量集合。
 * <p>
 * 该类只保存跨 starter、跨业务模块都可能复用的基础字符串字面量，避免各模块重复硬编码相同符号。
 * 所有常量均为无状态常量，不会读取配置、不会产生远程调用，也不会影响 Spring 容器启动流程。
 * </p>
 */
public class StringConstants {

    /**
     * 空字符串 {@code ""}
     */
    public static final String EMPTY = "";

    /**
     * 空格符 {@code " "}
     */
    public static final String SPACE = " ";

    /**
     * 制表符 {@code "\t"}
     */
    public static final String TAB = "	";

    /**
     * 空 JSON {@code "{}"}
     */
    public static final String EMPTY_JSON = "{}";

    /**
     * 点 {@code "."}
     */
    public static final String DOT = ".";

    /**
     * 双点 {@code ".."}
     * <p>
     * 作为指向上级文件夹的路径，如：{@code "../path"}
     * </p>
     */
    public static final String DOUBLE_DOT = "..";

    /**
     * 逗号 {@code ","}
     */
    public static final String COMMA = ",";

    /**
     * 中文逗号 {@code "，"}
     */
    public static final String CHINESE_COMMA = "，";

    /**
     * 冒号 {@code ":"}
     */
    public static final String COLON = ":";

    /**
     * 分号 {@code ";"}
     */
    public static final String SEMICOLON = ";";

    /**
     * 问号 {@code "?"}
     */
    public static final String QUESTION_MARK = "?";

    /**
     * 下划线 {@code "_"}
     */
    public static final String UNDERLINE = "_";

    /**
     * 减号（连接符） {@code "-"}
     */
    public static final String DASHED = "-";

    /**
     * 加号 {@code "+"}
     */
    public static final String PLUS = "+";

    /**
     * 等号 {@code "="}
     */
    public static final String EQUALS = "=";

    /**
     * 星号 {@code "*"}
     */
    public static final String ASTERISK = "*";

    /**
     * 斜杠 {@code "/"}
     */
    public static final String SLASH = "/";

    /**
     * 双斜杠 {@code "//"}
     */
    public static final String DOUBLE_SLASH = "//";

    /**
     * 反斜杠 {@code "\\"}
     */
    public static final String BACKSLASH = "\\";

    /**
     * 管道符 {@code "|"}
     */
    public static final String PIPE = "|";

    /**
     * 艾特 {@code "@"}
     */
    public static final String AT = "@";

    /**
     * 与符号 {@code "&"}
     */
    public static final String AMP = "&";

    /**
     * 花括号（左） <code>"{"</code>
     */
    public static final String DELIM_START = "{";

    /**
     * 花括号（右） <code>"}"</code>
     */
    public static final String DELIM_END = "}";

    /**
     * 中括号（左） {@code "["}
     */
    public static final String BRACKET_START = "[";

    /**
     * 中括号（右） {@code "]"}
     */
    public static final String BRACKET_END = "]";

    /**
     * 圆括号（左） {@code "("}
     */
    public static final String ROUND_BRACKET_START = "(";

    /**
     * 圆括号（右） {@code ")"}
     */
    public static final String ROUND_BRACKET_END = ")";

    /**
     * 双引号 {@code "\""}
     */
    public static final String DOUBLE_QUOTES = "\"";

    /**
     * 单引号 {@code "'"}
     */
    public static final String SINGLE_QUOTE = "'";

    /**
     * 回车符 {@code "\r"}
     */
    public static final String CR = "\r";

    /**
     * 换行符 {@code "\n"}
     */
    public static final String LF = "\n";

    /**
     * 路径模式 {@code "/**"}
     */
    public static final String PATH_PATTERN = "/**";

    /**
     * 路径模式（仅匹配当前目录） {@code "/*"}
     */
    public static final String PATH_PATTERN_CURRENT_DIR = "/*";

    /**
     * HTML 不间断空格转义 {@code "&nbsp;" -> " "}
     */
    public static final String HTML_NBSP = "&nbsp;";

    /**
     * HTML And 符转义 {@code "&amp;" -> "&"}
     */
    public static final String HTML_AMP = "&amp;";

    /**
     * HTML 双引号转义 {@code "&quot;" -> "\""}
     */
    public static final String HTML_QUOTE = "&quot;";

    /**
     * HTML 单引号转义 {@code "&apos" -> "'"}
     */
    public static final String HTML_APOS = "&apos;";

    /**
     * HTML 小于号转义 {@code "&lt;" -> "<"}
     */
    public static final String HTML_LT = "&lt;";

    /**
     * HTML 大于号转义 {@code "&gt;" -> ">"}
     */
    public static final String HTML_GT = "&gt;";

    /**
     * 私有构造方法。
     * <p>
     * 常量类不允许被实例化，调用方应直接通过类名访问公开常量。
     * </p>
     */
    private StringConstants() {
    }
}
