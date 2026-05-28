package com.vanta.starter.encrypt.context;

import com.vanta.starter.encrypt.encryptor.IEncryptor;
import com.vanta.starter.encrypt.enums.Algorithm;

import java.util.Objects;

/**
 * 加密上下文
 */
public class CryptoContext {

    /**
     * 加密/解密处理器
     * <p>
     * 优先级高于加密/解密算法
     * </p>
     */
    Class<? extends IEncryptor> encryptor;
    /**
     * 默认算法
     */
    private Algorithm algorithm;
    /**
     * 对称加密算法密钥
     */
    private String password;

    /**
     * 非对称加密算法公钥
     */
    private String publicKey;

    /**
     * 非对称加密算法私钥
     */
    private String privateKey;

    /**
     * 读取 Algorithm 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * 设置 Algorithm 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param algorithm algorithm 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
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

    /**
     * 设置 Encryptor 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param encryptor encryptor 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public void setEncryptor(Class<? extends IEncryptor> encryptor) {
        this.encryptor = encryptor;
    }

    /**
     * 读取 Password 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置 Password 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param password password 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 读取 Public Key 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置 Public Key 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param publicKey publicKey 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * 读取 Private Key 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置 Private Key 配置值。
     * 该方法主要服务于 Spring Boot 配置绑定和测试装配，应保持简单赋值语义。
     *
     * @param privateKey privateKey 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * 执行 equals 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param o o 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CryptoContext that = (CryptoContext) o;
        return algorithm == that.algorithm && Objects.equals(encryptor, that.encryptor) && Objects
                .equals(password, that.password) && Objects.equals(publicKey, that.publicKey) && Objects
                .equals(privateKey, that.privateKey);
    }

    /**
     * 执行 hashCode 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public int hashCode() {
        return Objects.hash(algorithm, encryptor, password, publicKey, privateKey);
    }
}
