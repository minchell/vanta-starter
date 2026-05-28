package com.vanta.starter.encrypt.encryptor;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import com.vanta.starter.encrypt.context.CryptoContext;

/**
 * PBEWithMD5AndDES（Password Based Encryption With MD5 And DES） 加密器
 * <p>
 * 混合加密算法，结合了 MD5 散列算法和 DES（Data Encryption Standard）加密算法
 * </p>
 */
public class PbeWithMd5AndDesEncryptor extends AbstractSymmetricCryptoEncryptor {

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param context context 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public PbeWithMd5AndDesEncryptor(CryptoContext context) {
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
        return SymmetricAlgorithm.PBEWithMD5AndDES;
    }
}
