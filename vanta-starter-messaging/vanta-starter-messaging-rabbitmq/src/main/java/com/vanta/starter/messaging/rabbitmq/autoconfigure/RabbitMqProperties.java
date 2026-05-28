package com.vanta.starter.messaging.rabbitmq.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ 基础能力配置。
 *
 * <p>配置前缀来自 {@link PropertiesConstants#RABBITMQ}，当前主要控制交换机、队列、绑定关系和消费者容器参数。
 * 该配置只描述应用希望声明的 RabbitMQ 资源，不包含账号、密码或服务地址；连接信息仍由 Spring Boot RabbitMQ 官方配置提供。</p>
 */
@ConfigurationProperties(prefix = PropertiesConstants.RABBITMQ)
public class RabbitMqProperties {

    /**
     * 是否启用 starter 自动声明交换机、队列和绑定关系。
     *
     * <p>该开关只控制 Vanta RabbitMQ 的资源声明动作，不等同于 Spring AMQP 自身是否可用。</p>
     */
    private boolean enabled = false;

    /**
     * 需要由 starter 自动注册到 Spring 容器的交换机配置列表。
     */
    private List<ExchangeConfig> exchanges = new ArrayList<>();

    /**
     * 需要由 starter 自动注册到 Spring 容器并按配置绑定交换机的队列配置列表。
     */
    private List<QueueConfig> queues = new ArrayList<>();

    /**
     * RabbitMQ 消费者容器默认参数。
     */
    @NestedConfigurationProperty
    private ConsumerConfig consumer = new ConsumerConfig();

    /**
     * 获取是否启用 starter 自动声明交换机、队列和绑定关系。
     *
     * @return 是否启用 starter 自动声明交换机、队列和绑定关系
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用 starter 自动声明交换机、队列和绑定关系。
     *
     * @param enabled 是否启用 starter 自动声明交换机、队列和绑定关系
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取需要由 starter 自动注册到 Spring 容器的交换机配置列表。
     *
     * @return 需要由 starter 自动注册到 Spring 容器的交换机配置列表
     */
    public List<ExchangeConfig> getExchanges() {
        return exchanges;
    }

    /**
     * 设置需要由 starter 自动注册到 Spring 容器的交换机配置列表。
     *
     * @param exchanges 需要由 starter 自动注册到 Spring 容器的交换机配置列表
     */
    public void setExchanges(List<ExchangeConfig> exchanges) {
        this.exchanges = exchanges;
    }

    /**
     * 获取需要由 starter 自动注册到 Spring 容器并按配置绑定交换机的队列配置列表。
     *
     * @return 需要由 starter 自动注册到 Spring 容器并按配置绑定交换机的队列配置列表
     */
    public List<QueueConfig> getQueues() {
        return queues;
    }

    /**
     * 设置需要由 starter 自动注册到 Spring 容器并按配置绑定交换机的队列配置列表。
     *
     * @param queues 需要由 starter 自动注册到 Spring 容器并按配置绑定交换机的队列配置列表
     */
    public void setQueues(List<QueueConfig> queues) {
        this.queues = queues;
    }

    /**
     * 获取 RabbitMQ 消费者容器默认参数。
     *
     * @return RabbitMQ 消费者容器默认参数
     */
    public ConsumerConfig getConsumer() {
        return consumer;
    }

    /**
     * 设置 RabbitMQ 消费者容器默认参数。
     *
     * @param consumer RabbitMQ 消费者容器默认参数
     */
    public void setConsumer(ConsumerConfig consumer) {
        this.consumer = consumer;
    }

    /**
     * RabbitMQ 交换机类型。
     */
    public enum ExchangeType {
        /**
         * 直连交换机，routingKey 需要精确匹配。
         */
        DIRECT,
        /**
         * 主题交换机，routingKey 支持 * 和 # 通配符。
         */
        TOPIC,
        /**
         * 广播交换机，消息会投递到所有绑定队列。
         */
        FANOUT,
        /**
         * 头交换机，根据消息 headers 条件匹配。
         */
        HEADERS,
        /**
         * 自定义交换机，通常依赖 RabbitMQ 插件提供具体类型。
         */
        CUSTOM,
    }

    /**
     * RabbitMQ 交换机声明配置。
     *
     * <p>每一项会被 {@link RabbitMqAutoConfiguration} 转换为 Direct、Topic、Fanout、Headers 或 Custom 交换机 Bean。</p>
     */
    public static class ExchangeConfig {

        /**
         * 交换机名称。
         *
         * <p>该名称会作为 RabbitMQ 资源名称，也会参与生成 Spring Bean 名称，因此同一应用内必须唯一。</p>
         */
        private String name;

        /**
         * 交换机类型。
         *
         * <p>默认使用直连交换机；如果设置为 {@link ExchangeType#CUSTOM}，必须同步提供 {@link #customType}。</p>
         */
        private ExchangeType type = ExchangeType.DIRECT;

        /**
         * 自定义交换机类型。
         *
         * <p>仅当 {@link #type} 为 {@link ExchangeType#CUSTOM} 时生效，用于声明 RabbitMQ 插件提供的交换机类型。</p>
         */
        private String customType;

        /**
         * 是否持久化交换机。
         *
         * <p>为 true 时 broker 重启后交换机仍保留；生产环境通常保持默认 true。</p>
         */
        private Boolean durable = Boolean.TRUE;

        /**
         * 是否在没有队列绑定后自动删除交换机。
         *
         * <p>共享交换机不建议开启，临时测试交换机可以按需开启。</p>
         */
        private Boolean autoDelete = Boolean.FALSE;

        /**
         * 交换机扩展参数。
         *
         * <p>用于传递 RabbitMQ 插件或自定义交换机需要的 x-arguments。</p>
         */
        private Map<String, Object> arguments;

        /**
         * 获取交换机名称。
         *
         * @return 交换机名称
         */
        public String getName() {
            return name;
        }

        /**
         * 设置交换机名称。
         *
         * @param name 交换机名称
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取交换机类型。
         *
         * @return 交换机类型
         */
        public ExchangeType getType() {
            return type;
        }

        /**
         * 设置交换机类型。
         *
         * @param type 交换机类型
         */
        public void setType(ExchangeType type) {
            this.type = type;
        }

        /**
         * 获取自定义交换机类型。
         *
         * @return 自定义交换机类型
         */
        public String getCustomType() {
            return customType;
        }

        /**
         * 设置自定义交换机类型。
         *
         * @param customType 自定义交换机类型
         */
        public void setCustomType(String customType) {
            this.customType = customType;
        }

        /**
         * 获取是否持久化交换机。
         *
         * @return 是否持久化交换机
         */
        public Boolean getDurable() {
            return durable;
        }

        /**
         * 设置是否持久化交换机。
         *
         * @param durable 是否持久化交换机
         */
        public void setDurable(Boolean durable) {
            this.durable = durable;
        }

        /**
         * 获取是否在没有队列绑定后自动删除交换机。
         *
         * @return 是否在没有队列绑定后自动删除交换机
         */
        public Boolean getAutoDelete() {
            return autoDelete;
        }

        /**
         * 设置是否在没有队列绑定后自动删除交换机。
         *
         * @param autoDelete 是否在没有队列绑定后自动删除交换机
         */
        public void setAutoDelete(Boolean autoDelete) {
            this.autoDelete = autoDelete;
        }

        /**
         * 获取交换机扩展参数。
         *
         * @return 交换机扩展参数
         */
        public Map<String, Object> getArguments() {
            return arguments;
        }

        /**
         * 设置交换机扩展参数。
         *
         * @param arguments 交换机扩展参数
         */
        public void setArguments(Map<String, Object> arguments) {
            this.arguments = arguments;
        }
    }

    /**
     * RabbitMQ 队列和绑定声明配置。
     *
     * <p>每一项至少声明一个队列；当 {@link #exchangeName} 非空时，会按交换机类型创建绑定关系。</p>
     */
    public static class QueueConfig {

        /**
         * 队列名称。
         *
         * <p>该名称会作为 RabbitMQ 队列名，也会参与生成 Spring Bean 名称，因此同一应用内必须唯一。</p>
         */
        private String name;

        /**
         * 队列要绑定的交换机名称。
         *
         * <p>支持使用英文逗号配置多个交换机名称；名称必须能在 {@link #exchanges} 中找到对应配置。</p>
         */
        private String exchangeName = "";

        /**
         * 绑定路由键。
         *
         * <p>Direct、Topic 和 Custom 交换机绑定时使用该值；Fanout 交换机会忽略该字段。</p>
         */
        private String routingKey = "";

        /**
         * 是否持久化队列。
         *
         * <p>为 true 时 broker 重启后队列仍保留；生产环境通常保持默认 true。</p>
         */
        private Boolean durable = Boolean.TRUE;

        /**
         * 是否为当前连接独占队列。
         *
         * <p>独占队列只适合临时消费或测试场景，业务共享队列不建议开启。</p>
         */
        private Boolean exclusive = Boolean.FALSE;

        /**
         * 是否在最后一个消费者断开后自动删除队列。
         *
         * <p>长期业务队列不建议开启，临时队列可以按需开启。</p>
         */
        private Boolean autoDelete = Boolean.FALSE;

        /**
         * Headers 交换机绑定时是否要求所有 header 条件都匹配。
         *
         * <p>true 表示 whereAll，false 表示 whereAny；仅在绑定 Headers 交换机时生效。</p>
         */
        private Boolean whereAll = Boolean.TRUE;

        /**
         * 队列扩展参数。
         *
         * <p>用于配置死信交换机、最大长度、过期时间等 RabbitMQ x-arguments。</p>
         */
        private Map<String, Object> args;

        /**
         * Headers 交换机绑定条件。
         *
         * <p>仅在队列绑定 Headers 交换机时使用，键值会被写入 header 匹配规则。</p>
         */
        private Map<String, Object> headers;

        /**
         * 获取队列名称。
         *
         * @return 队列名称
         */
        public String getName() {
            return name;
        }

        /**
         * 设置队列名称。
         *
         * @param name 队列名称
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取队列要绑定的交换机名称。
         *
         * @return 队列要绑定的交换机名称
         */
        public String getExchangeName() {
            return exchangeName;
        }

        /**
         * 设置队列要绑定的交换机名称。
         *
         * @param exchangeName 队列要绑定的交换机名称
         */
        public void setExchangeName(String exchangeName) {
            this.exchangeName = exchangeName;
        }

        /**
         * 获取绑定路由键。
         *
         * @return 绑定路由键
         */
        public String getRoutingKey() {
            return routingKey;
        }

        /**
         * 设置绑定路由键。
         *
         * @param routingKey 绑定路由键
         */
        public void setRoutingKey(String routingKey) {
            this.routingKey = routingKey;
        }

        /**
         * 获取是否持久化队列。
         *
         * @return 是否持久化队列
         */
        public Boolean getDurable() {
            return durable;
        }

        /**
         * 设置是否持久化队列。
         *
         * @param durable 是否持久化队列
         */
        public void setDurable(Boolean durable) {
            this.durable = durable;
        }

        /**
         * 获取是否为当前连接独占队列。
         *
         * @return 是否为当前连接独占队列
         */
        public Boolean getExclusive() {
            return exclusive;
        }

        /**
         * 设置是否为当前连接独占队列。
         *
         * @param exclusive 是否为当前连接独占队列
         */
        public void setExclusive(Boolean exclusive) {
            this.exclusive = exclusive;
        }

        /**
         * 获取是否在最后一个消费者断开后自动删除队列。
         *
         * @return 是否在最后一个消费者断开后自动删除队列
         */
        public Boolean getAutoDelete() {
            return autoDelete;
        }

        /**
         * 设置是否在最后一个消费者断开后自动删除队列。
         *
         * @param autoDelete 是否在最后一个消费者断开后自动删除队列
         */
        public void setAutoDelete(Boolean autoDelete) {
            this.autoDelete = autoDelete;
        }

        /**
         * 获取 Headers 交换机绑定时是否要求所有 header 条件都匹配。
         *
         * @return Headers 交换机绑定时是否要求所有 header 条件都匹配
         */
        public Boolean getWhereAll() {
            return whereAll;
        }

        /**
         * 设置 Headers 交换机绑定时是否要求所有 header 条件都匹配。
         *
         * @param whereAll Headers 交换机绑定时是否要求所有 header 条件都匹配
         */
        public void setWhereAll(Boolean whereAll) {
            this.whereAll = whereAll;
        }

        /**
         * 获取队列扩展参数。
         *
         * @return 队列扩展参数
         */
        public Map<String, Object> getArgs() {
            return args;
        }

        /**
         * 设置队列扩展参数。
         *
         * @param args 队列扩展参数
         */
        public void setArgs(Map<String, Object> args) {
            this.args = args;
        }

        /**
         * 获取 Headers 交换机绑定条件。
         *
         * @return Headers 交换机绑定条件
         */
        public Map<String, Object> getHeaders() {
            return headers;
        }

        /**
         * 设置 Headers 交换机绑定条件。
         *
         * @param headers Headers 交换机绑定条件
         */
        public void setHeaders(Map<String, Object> headers) {
            this.headers = headers;
        }
    }

    /**
     * RabbitMQ 监听容器默认配置。
     *
     * <p>这些参数会应用到 starter 创建的 {@code SimpleRabbitListenerContainerFactory}。</p>
     */
    public static class ConsumerConfig {

        /**
         * 初始消费者线程数。
         */
        private Integer concurrentConsumers = 3;

        /**
         * 最大消费者线程数。
         */
        private Integer maxConcurrentConsumers = 10;

        /**
         * 单个消费者一次预取的消息数量。
         */
        private Integer prefetchCount = 1;

        /**
         * 应用启动时是否自动启动监听容器。
         */
        private Boolean autoStartup = true;

        /**
         * 获取初始消费者线程数。
         *
         * @return 初始消费者线程数
         */
        public Integer getConcurrentConsumers() {
            return concurrentConsumers;
        }

        /**
         * 设置初始消费者线程数。
         *
         * @param concurrentConsumers 初始消费者线程数
         */
        public void setConcurrentConsumers(Integer concurrentConsumers) {
            this.concurrentConsumers = concurrentConsumers;
        }

        /**
         * 获取最大消费者线程数。
         *
         * @return 最大消费者线程数
         */
        public Integer getMaxConcurrentConsumers() {
            return maxConcurrentConsumers;
        }

        /**
         * 设置最大消费者线程数。
         *
         * @param maxConcurrentConsumers 最大消费者线程数
         */
        public void setMaxConcurrentConsumers(Integer maxConcurrentConsumers) {
            this.maxConcurrentConsumers = maxConcurrentConsumers;
        }

        /**
         * 获取单个消费者一次预取的消息数量。
         *
         * @return 单个消费者一次预取的消息数量
         */
        public Integer getPrefetchCount() {
            return prefetchCount;
        }

        /**
         * 设置单个消费者一次预取的消息数量。
         *
         * @param prefetchCount 单个消费者一次预取的消息数量
         */
        public void setPrefetchCount(Integer prefetchCount) {
            this.prefetchCount = prefetchCount;
        }

        /**
         * 获取应用启动时是否自动启动监听容器。
         *
         * @return 应用启动时是否自动启动监听容器
         */
        public Boolean getAutoStartup() {
            return autoStartup;
        }

        /**
         * 设置应用启动时是否自动启动监听容器。
         *
         * @param autoStartup 应用启动时是否自动启动监听容器
         */
        public void setAutoStartup(Boolean autoStartup) {
            this.autoStartup = autoStartup;
        }
    }
}
