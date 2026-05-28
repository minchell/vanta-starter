package com.vanta.starter.encrypt.encryptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.encrypt.context.CryptoContext;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对称加密器
 */
public abstract class AbstractSymmetricCryptoEncryptor extends AbstractEncryptor {

    /**
     * 对称加密缓存
     */
    private static final Map<String, SymmetricCrypto> CACHE = new ConcurrentHashMap<>();

    /**
     * 加密上下文
     */
    private final CryptoContext context;

    protected AbstractSymmetricCryptoEncryptor(CryptoContext context) {
        super(context);
        this.context = context;
    }

    /**
     * 执行 encrypt 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param plaintext plaintext 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public String encrypt(String plaintext) {
        if (CharSequenceUtil.isBlank(plaintext)) {
            return plaintext;
        }
        return this.getCrypto(context.getPassword()).encryptHex(plaintext);
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
        if (CharSequenceUtil.isBlank(ciphertext)) {
            return ciphertext;
        }
        return this.getCrypto(context.getPassword()).decryptStr(ciphertext);
    }

    /**
     * 获取对称加密算法
     *
     * @param password 密钥
     * @return 对称加密算法
     */
    protected SymmetricCrypto getCrypto(String password) {
        SymmetricAlgorithm algorithm = this.getAlgorithm();
        String key = algorithm + StringConstants.UNDERLINE + password;
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }
        SymmetricCrypto symmetricCrypto = new SymmetricCrypto(algorithm, password.getBytes(StandardCharsets.UTF_8));
        CACHE.put(key, symmetricCrypto);
        return symmetricCrypto;
    }

    /**
     * 获取对称加密算法类型
     *
     * @return 对称加密算法类型
     */
    protected abstract SymmetricAlgorithm getAlgorithm();
}
