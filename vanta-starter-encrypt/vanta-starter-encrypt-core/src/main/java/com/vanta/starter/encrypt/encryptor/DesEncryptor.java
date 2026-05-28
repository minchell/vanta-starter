package com.vanta.starter.encrypt.encryptor;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.vanta.starter.encrypt.context.CryptoContext;

/**
 * DES（Data Encryption Standard） 加密器
 * <p>
 * 一种对称加密算法，使用相同的密钥进行加密和解密。DES 使用 56 位密钥（实际上有 64 位，但有 8 位用于奇偶校验）和一系列置换和替换操作来加密数据。
 * </p>
 */
public class DesEncryptor extends AbstractSymmetricCryptoEncryptor {

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param context context 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public DesEncryptor(CryptoContext context) {
        super(context);
    }

    /**
     * 读取 Algorithm 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    protected SymmetricAlgorithm getAlgorithm() {
        return SymmetricAlgorithm.DES;
    }
}
