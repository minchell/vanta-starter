package com.vanta.starter.encrypt.encryptor;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.vanta.starter.encrypt.context.CryptoContext;

/**
 * RSA 加密器
 * <p>
 * 非对称加密算法，由罗纳德·李维斯特（Ron Rivest）、阿迪·沙米尔（Adi Shamir）和伦纳德·阿德曼（Leonard Adleman）于1977年提出，安全性基于大数因子分解问题的困难性。
 * </p>
 */
public class RsaEncryptor extends AbstractEncryptor {

    /**
     * 加密上下文
     */
    private final CryptoContext context;

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param context context 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public RsaEncryptor(CryptoContext context) {
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
        return Base64.encode(SecureUtil.rsa(null, context.getPublicKey()).encrypt(plaintext, KeyType.PublicKey));
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
        return new String(SecureUtil.rsa(context.getPrivateKey(), null).decrypt(Base64.decode(ciphertext), KeyType.PrivateKey));
    }
}
