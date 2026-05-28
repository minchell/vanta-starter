package com.vanta.starter.encrypt.encryptor;

import cn.hutool.core.codec.Base64;

/**
 * Base64 加密器
 * <p>
 * 一种用于编码二进制数据到文本格式的算法，常用于邮件附件、网页传输等场合，但它不是一种加密算法，只提供数据的编码和解码，不保证数据的安全性。
 * </p>
 */
public class Base64Encryptor implements IEncryptor {

    /**
     * 执行 encrypt 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param plaintext plaintext 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String encrypt(String plaintext) {
        return Base64.encode(plaintext);
    }

    /**
     * 执行 decrypt 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param ciphertext ciphertext 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String decrypt(String ciphertext) {
        return Base64.decodeStr(ciphertext);
    }
}
