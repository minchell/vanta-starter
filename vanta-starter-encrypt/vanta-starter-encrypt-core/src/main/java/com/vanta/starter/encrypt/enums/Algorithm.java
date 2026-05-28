package com.vanta.starter.encrypt.enums;

import com.vanta.starter.encrypt.encryptor.AesEncryptor;
import com.vanta.starter.encrypt.encryptor.Base64Encryptor;
import com.vanta.starter.encrypt.encryptor.DesEncryptor;
import com.vanta.starter.encrypt.encryptor.IEncryptor;
import com.vanta.starter.encrypt.encryptor.PbeWithMd5AndDesEncryptor;
import com.vanta.starter.encrypt.encryptor.RsaEncryptor;

/**
 * 加密算法枚举
 */
public enum Algorithm {

    /**
     * 默认使用配置属性的算法
     */
    DEFAULT(null),

    /**
     * AES
     */
    AES(AesEncryptor.class),

    /**
     * DES
     */
    DES(DesEncryptor.class),

    /**
     * PBE With MD5 And DES
     */
    PBE_WITH_MD5_AND_DES(PbeWithMd5AndDesEncryptor.class),

    /**
     * RSA
     */
    RSA(RsaEncryptor.class),

    /**
     * Base64
     */
    BASE64(Base64Encryptor.class);

    /**
     * 加密/解密处理器
     */
    private final Class<? extends IEncryptor> encryptor;

    Algorithm(Class<? extends IEncryptor> encryptor) {
        this.encryptor = encryptor;
    }

    /**
     * 读取 Encryptor 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public Class<? extends IEncryptor> getEncryptor() {
        return encryptor;
    }
}
