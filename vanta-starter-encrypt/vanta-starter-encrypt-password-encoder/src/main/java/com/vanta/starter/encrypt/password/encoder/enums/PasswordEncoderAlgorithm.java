package com.vanta.starter.encrypt.password.encoder.enums;

import java.util.regex.Pattern;

/**
 * 密码编码器加密算法枚举
 */
public enum PasswordEncoderAlgorithm {

    /**
     * BCrypt加密算法
     */
    BCRYPT(Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}")),

    /**
     * SCrypt加密算法
     */
    SCRYPT(Pattern.compile("\\A\\$s0\\$[0-9a-f]+\\$[0-9a-f]+\\$[0-9a-f]+")),

    /**
     * PBKDF2加密算法
     */
    PBKDF2(Pattern.compile("\\A\\$pbkdf2-sha256\\$\\d+\\$[0-9a-f]+\\$[0-9a-f]+")),

    /**
     * Argon2加密算法
     */
    ARGON2(Pattern.compile("\\A\\$argon2(id|i|d)\\$v=\\d+\\$m=\\d+,t=\\d+,p=\\d+\\$[0-9a-zA-Z+/]+\\$[0-9a-zA-Z+/]+"));

    /**
     * 正则匹配
     * pattern 字段。
     * <p>用于保存 加密能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final Pattern pattern;

    PasswordEncoderAlgorithm(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * 读取 Pattern 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public Pattern getPattern() {
        return pattern;
    }
}
