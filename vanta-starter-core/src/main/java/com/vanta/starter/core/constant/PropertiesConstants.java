package com.vanta.starter.core.constant;

/**
 * Vanta Starter 配置前缀常量集合。
 * <p>
 * 该类统一维护所有 starter 自动配置使用的配置项前缀，避免不同模块拼写不一致。
 * 这些常量只描述配置命名空间，本身不会创建 Bean、不会连接外部服务，也不会产生任何远程副作用。
 * </p>
 */
public final class PropertiesConstants {

    /**
     * 所有 Vanta Starter 配置的根前缀。
     */
    public static final String VANTA_STARTER = "vanta-starter";

    /**
     * starter 通用启用开关字段名。
     */
    public static final String ENABLED = "enabled";

    /**
     * Web starter 配置前缀。
     */
    public static final String WEB = VANTA_STARTER + StringConstants.DOT + "web";

    /**
     * Web 服务基础架构配置前缀。
     */
    public static final String WEB_SERVICE = VANTA_STARTER + StringConstants.DOT + "web-service";

    /**
     * API Doc starter 配置前缀。
     */
    public static final String API_DOC = VANTA_STARTER + StringConstants.DOT + "api-doc";

    /**
     * Web 跨域配置前缀。
     */
    public static final String WEB_CORS = WEB + StringConstants.DOT + "cors";

    /**
     * Web 统一响应包装配置前缀。
     */
    public static final String WEB_RESPONSE = WEB + StringConstants.DOT + "response";

    /**
     * JustAuth 第三方登录配置前缀。
     */
    public static final String AUTH_JUSTAUTH = VANTA_STARTER + StringConstants.DOT + "justauth";

    /**
     * 缂撳瓨 starter 閰嶇疆鍓嶇紑銆?
     */
    public static final String CACHE = VANTA_STARTER + StringConstants.DOT + "cache";

    /**
     * Spring Cache starter 閰嶇疆鍓嶇紑銆?
     */
    public static final String CACHE_SPRING_CACHE = CACHE + StringConstants.DOT + "spring-cache";

    /**
     * JetCache starter 閰嶇疆鍓嶇紑銆?
     */
    public static final String CACHE_JETCACHE = CACHE + StringConstants.DOT + "jetcache";

    /**
     * 加密 starter 配置前缀。
     */
    public static final String ENCRYPT = VANTA_STARTER + StringConstants.DOT + "encrypt";

    /**
     * 密码编码器配置前缀。
     */
    public static final String ENCRYPT_PASSWORD_ENCODER = ENCRYPT + StringConstants.DOT + "password-encoder";

    /**
     * 字段级加解密配置前缀。
     */
    public static final String ENCRYPT_FIELD = ENCRYPT + StringConstants.DOT + "field";

    /**
     * API 报文加解密配置前缀。
     */
    public static final String ENCRYPT_API = ENCRYPT + StringConstants.DOT + "api";

    /**
     * 安全 starter 配置前缀。
     */
    public static final String SECURITY = VANTA_STARTER + StringConstants.DOT + "security";

    /**
     * XSS 防护配置前缀。
     */
    public static final String SECURITY_XSS = SECURITY + StringConstants.DOT + "xss";

    /**
     * 敏感词过滤配置前缀。
     */
    public static final String SECURITY_SENSITIVE_WORDS = SECURITY + StringConstants.DOT + "sensitive-words";

    /**
     * 限流 starter 配置前缀。
     */
    public static final String RATE_LIMITER = VANTA_STARTER + StringConstants.DOT + "rate-limiter";

    /**
     * RabbitMQ starter 配置前缀。
     */
    public static final String RABBITMQ = VANTA_STARTER + StringConstants.DOT + "rabbitmq";

    /**
     * RabbitMQ 延迟消息配置前缀。
     */
    public static final String RABBITMQ_DELAY = RABBITMQ + StringConstants.DOT + "delay";

    /**
     * RocketMQ starter 配置前缀。
     */
    public static final String ROCKETMQ = VANTA_STARTER + StringConstants.DOT + "rocketmq";

    /**
     * Kafka starter 配置前缀。
     */
    public static final String KAFKA = VANTA_STARTER + StringConstants.DOT + "kafka";

    /**
     * InfluxDB starter 配置前缀。
     */
    public static final String INFLUXDB = VANTA_STARTER + StringConstants.DOT + "influxdb";

    /**
     * ZooKeeper starter 配置前缀。
     */
    public static final String ZOOKEEPER = VANTA_STARTER + StringConstants.DOT + "zookeeper";

    /**
     * Nacos starter 配置前缀。
     */
    public static final String NACOS = VANTA_STARTER + StringConstants.DOT + "nacos";

    /**
     * Nacos 通用客户端配置前缀。
     */
    public static final String NACOS_CLIENT = NACOS + StringConstants.DOT + "client";

    /**
     * Elasticsearch starter 配置前缀。
     */
    public static final String ELASTICSEARCH = VANTA_STARTER + StringConstants.DOT + "elasticsearch";

    /**
     * 观测 starter 配置前缀。
     */
    public static final String OBSERVABILITY = VANTA_STARTER + StringConstants.DOT + "observability";

    /**
     * 分布式锁 starter 配置前缀。
     */
    public static final String LOCK = VANTA_STARTER + StringConstants.DOT + "lock";

    /**
     * 存储 starter 配置前缀。
     */
    public static final String STORAGE = VANTA_STARTER + StringConstants.DOT + "storage";

    /**
     * 本地文件存储配置前缀。
     */
    public static final String STORAGE_LOCAL = STORAGE + StringConstants.DOT + "local";

    /**
     * 任务调度 starter 配置前缀。
     */
    public static final String SCHEDULER = VANTA_STARTER + StringConstants.DOT + "scheduler";

    /**
     * 幂等 starter 配置前缀。
     */
    public static final String IDEMPOTENT = VANTA_STARTER + StringConstants.DOT + "idempotent";

    /**
     * 链路追踪 starter 配置前缀。
     */
    public static final String TRACE = VANTA_STARTER + StringConstants.DOT + "trace";

    /**
     * 验证码 starter 配置前缀。
     */
    public static final String CAPTCHA = VANTA_STARTER + StringConstants.DOT + "captcha";

    /**
     * 图形验证码配置前缀。
     */
    public static final String CAPTCHA_GRAPHIC = CAPTCHA + StringConstants.DOT + "graphic";

    /**
     * 行为验证码配置前缀。
     */
    public static final String CAPTCHA_BEHAVIOR = CAPTCHA + StringConstants.DOT + "behavior";

    /**
     * 消息 starter 配置前缀。
     */
    public static final String MESSAGING = VANTA_STARTER + StringConstants.DOT + "messaging";

    /**
     * WebSocket 消息配置前缀。
     */
    public static final String MESSAGING_WEBSOCKET = MESSAGING + StringConstants.DOT + "websocket";

    /**
     * MQTT 消息配置前缀。
     */
    public static final String MESSAGING_MQTT = MESSAGING + StringConstants.DOT + "mqtt";

    /**
     * 日志 starter 配置前缀。
     */
    public static final String LOG = VANTA_STARTER + StringConstants.DOT + "log";

    /**
     * License 授权 starter 配置前缀。
     */
    public static final String LICENSE = VANTA_STARTER + StringConstants.DOT + "license";

    /**
     * License 生成器配置前缀。
     */
    public static final String LICENSE_GENERATOR = LICENSE + StringConstants.DOT + "generator";

    /**
     * License 校验器配置前缀。
     */
    public static final String LICENSE_VERIFIER = LICENSE + StringConstants.DOT + "verifier";

    /**
     * CRUD starter 配置前缀。
     */
    public static final String CRUD = VANTA_STARTER + StringConstants.DOT + "crud";

    /**
     * 数据权限 starter 配置前缀。
     */
    public static final String DATA_PERMISSION = VANTA_STARTER + StringConstants.DOT + "data-permission";

    /**
     * 多租户 starter 配置前缀。
     */
    public static final String TENANT = VANTA_STARTER + StringConstants.DOT + "tenant";

    /**
     * 私有构造方法。
     * <p>
     * 配置前缀类仅作为常量命名空间使用，禁止实例化。
     * </p>
     */
    private PropertiesConstants() {
    }
}
