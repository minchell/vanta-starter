package com.vanta.starter.encrypt.password.encoder.encryptor;

import cn.hutool.extra.spring.SpringUtil;
import com.vanta.starter.core.util.SpringUtils;
import com.vanta.starter.encrypt.context.CryptoContext;
import com.vanta.starter.encrypt.encryptor.AbstractEncryptor;
import com.vanta.starter.encrypt.password.encoder.autoconfigure.PasswordEncoderProperties;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器加密器
 *
 * <p>
 * 使用前必须注入 {@link PasswordEncoder}，此加密方式不可逆，适合于密码场景
 * </p>
 *
 * @see PasswordEncoder
 * @see PasswordEncoderProperties
 */
public class PasswordEncoderEncryptor extends AbstractEncryptor {

    /**
     * properties 字段。
     * <p>用于保存 加密能力 的扩展属性集合，用于承载业务方按需补充的非固定配置。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private final PasswordEncoderProperties properties = SpringUtils.getBean(PasswordEncoderProperties.class, true);

    /**
     * 创建当前类型实例。
     * 构造参数仅用于注入配置或底层依赖，构造阶段不应主动访问远程服务。
     *
     * @param context context 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public PasswordEncoderEncryptor(CryptoContext context) {
        super(context);
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
        // 如果已经是加密格式，直接返回
        if (properties == null || properties.getAlgorithm().getPattern().matcher(plaintext).matches()) {
            return plaintext;
        }
        return SpringUtil.getBean(PasswordEncoder.class).encode(plaintext);
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
        return ciphertext;
    }
}
