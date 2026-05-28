package com.vanta.starter.encrypt.field.annotation;

import com.vanta.starter.encrypt.encryptor.IEncryptor;
import com.vanta.starter.encrypt.enums.Algorithm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段加密注解
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldEncrypt {

    /**
     * 加密算法
     */
    Algorithm value() default Algorithm.DEFAULT;

    /**
     * 加密处理器
     * <p>
     * 优先级高于加密算法
     * </p>
     */
    Class<? extends IEncryptor> encryptor() default IEncryptor.class;

    /**
     * 对称加密算法密钥
     */
    String password() default "";

    /**
     * 非对称加密算法公钥：RSA需要
     */
    String publicKey() default "";

    /**
     * 非对称加密算法私钥：RSA需要
     */
    String privateKey() default "";
}