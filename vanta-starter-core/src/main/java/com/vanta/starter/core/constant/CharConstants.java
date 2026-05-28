package com.vanta.starter.core.constant;

/**
 * 字符常量集合。
 * <p>
 * 该类提供常见标点、空白符和路径符号的字符形式，适合在字符串扫描、解析、拼接等轻量工具逻辑中复用。
 * 所有字段都是 JVM 内常量，不依赖外部配置，也不会引入任何运行时副作用。
 * </p>
 */
public class CharConstants {

    /**
     * 空格符 {@code ' '}
     */
    public static final char SPACE = ' ';

    /**
     * 制表符 {@code '\t'}
     */
    public static final char TAB = '	';

    /**
     * 点 {@code '.'}
     */
    public static final char DOT = '.';

    /**
     * 逗号 {@code ','}
     */
    public static final char COMMA = ',';

    /**
     * 中文逗号 {@code '，'}
     */
    public static final char CHINESE_COMMA = '，';

    /**
     * 冒号 {@code ':'}
     */
    public static final char COLON = ':';

    /**
     * 分号 {@code ';'}
     */
    public static final char SEMICOLON = ';';

    /**
     * 问号 {@code '?'}
     */
    public static final char QUESTION_MARK = '?';

    /**
     * 下划线 {@code '_'}
     */
    public static final char UNDERLINE = '_';

    /**
     * 减号（连接符） {@code '-'}
     */
    public static final char DASHED = '-';

    /**
     * 加号 {@code '+'}
     */
    public static final char PLUS = '+';

    /**
     * 等号 {@code '='}
     */
    public static final char EQUALS = '=';

    /**
     * 星号 {@code '*'}
     */
    public static final char ASTERISK = '*';

    /**
     * 斜杠 {@code '/'}
     */
    public static final char SLASH = '/';

    /**
     * 反斜杠 {@code '\\'}
     */
    public static final char BACKSLASH = '\\';

    /**
     * 管道符 {@code '|'}
     */
    public static final char PIPE = '|';

    /**
     * 艾特 {@code '@'}
     */
    public static final char AT = '@';

    /**
     * 与符号 {@code '&'}
     */
    public static final char AMP = '&';

    /**
     * 花括号（左） <code>'{'</code>
     */
    public static final char DELIM_START = '{';

    /**
     * 花括号（右） <code>'}'</code>
     */
    public static final char DELIM_END = '}';

    /**
     * 中括号（左） {@code '['}
     */
    public static final char BRACKET_START = '[';

    /**
     * 中括号（右） {@code ']'}
     */
    public static final char BRACKET_END = ']';

    /**
     * 圆括号（左） {@code '('}
     */
    public static final char ROUND_BRACKET_START = '(';

    /**
     * 圆括号（右） {@code ')'}
     */
    public static final char ROUND_BRACKET_END = ')';

    /**
     * 双引号 {@code '"'}
     */
    public static final char DOUBLE_QUOTES = '"';

    /**
     * 单引号 {@code '\''}
     */
    public static final char SINGLE_QUOTE = '\'';

    /**
     * 回车符 {@code '\r'}
     */
    public static final char CR = '\r';

    /**
     * 换行符 {@code '\n'}
     */
    public static final char LF = '\n';

    /**
     * 私有构造方法。
     * <p>
     * 常量类不承载对象状态，禁止实例化可以避免调用方误以为该类存在可变行为。
     * </p>
     */
    private CharConstants() {
    }
}
