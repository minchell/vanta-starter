package com.vanta.starter.encrypt.encryptor;

/**
 * 加密器接口
 */
public interface IEncryptor {

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return 加密后的文本
     */
    String encrypt(String plaintext);

    /**
     * 解密
     *
     * @param ciphertext 密文
     * @return 解密后的文本
     */
    String decrypt(String ciphertext);
}
