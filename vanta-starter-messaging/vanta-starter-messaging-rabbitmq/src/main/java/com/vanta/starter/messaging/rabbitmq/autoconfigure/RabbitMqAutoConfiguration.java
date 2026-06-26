package com.vanta.starter.messaging.rabbitmq.autoconfigure;

import com.vanta.starter.core.constant.PropertiesConstants;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.messaging.rabbitmq.util.SpringBeanUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

/**
 * RabbitMQ 基础自动配置。
 *
 * <p>该配置负责装配 RabbitAdmin、RabbitTemplate、监听容器工厂，并在
 * {@code vanta-starter.rabbitmq.enabled=true} 时按配置声明交换机、队列和绑定关系。
 * 所有可替换基础设施 Bean 都使用缺省条件，业务项目可以优先声明自己的 Bean 覆盖 starter 默认实现。</p>
 */
@AutoConfiguration
@EnableRabbit
@ConditionalOnClass({ConnectionFactory.class})
@ConditionalOnProperty(prefix = PropertiesConstants.RABBITMQ, name = PropertiesConstants.ENABLED, havingValue = "true")
@EnableConfigurationProperties({RabbitMqProperties.class, RabbitMqDelayProperties.class})
@Import({MessageConverterConfig.class})
public class RabbitMqAutoConfiguration {

    /**
     * starter 动态注册队列 Bean 时使用的名称后缀。
     */
    protected final static String QUEUE_SUFFIX = "Queue";
    /**
     * starter 动态注册交换机 Bean 时使用的名称后缀。
     */
    protected final static String EXCHANGE_SUFFIX = "Exchange";
    /**
     * starter 动态注册绑定 Bean 时使用的名称后缀。
     */
    protected final static String BINDING_SUFFIX = "Binding";
    /**
     * RabbitMQ 自动配置日志。
     */
    private static final Logger log = LoggerFactory.getLogger(RabbitMqAutoConfiguration.class);
    /**
     * RabbitMQ 基础能力配置。
     */
    private final RabbitMqProperties properties;

    /**
     * 创建 RabbitMQ 自动配置。
     *
     * @param properties RabbitMQ 基础能力配置
     */
    public RabbitMqAutoConfiguration(RabbitMqProperties properties) {
        this.properties = properties;
    }

    /**
     * 注册 Spring Bean 动态访问工具。
     *
     * <p>该 Bean 需要以静态工厂方法提前进入 BeanFactoryPostProcessor 阶段，否则后续根据配置动态注册
     * RabbitMQ 交换机、队列和绑定时无法拿到 {@code ConfigurableListableBeanFactory}。</p>
     *
     * @return Spring Bean 动态访问工具
     */
    @Bean
    @ConditionalOnMissingBean
    public static SpringBeanUtil springBeanUtil() {
        return new SpringBeanUtil();
    }

    /**
     * 注册 RabbitAdmin。
     *
     * <p>业务方没有声明 RabbitAdmin 时使用该默认 Bean，便于 Spring AMQP 执行队列、交换机和绑定声明。</p>
     *
     * @param connectionFactory RabbitMQ 连接工厂
     * @return RabbitAdmin 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    /**
     * 注册 RabbitMQ 监听容器工厂。
     *
     * <p>该工厂会应用 {@link RabbitMqProperties.ConsumerConfig} 中的并发、预取和自启动配置。</p>
     *
     * @param connectionFactory RabbitMQ 连接工厂
     * @param messageConverter  消息转换器
     * @return RabbitMQ 监听容器工厂
     */
    @Bean
    @ConditionalOnMissingBean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitMqProperties.ConsumerConfig cfg = properties.getConsumer();
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(cfg.getConcurrentConsumers());
        factory.setMaxConcurrentConsumers(cfg.getMaxConcurrentConsumers());
        factory.setPrefetchCount(cfg.getPrefetchCount());
        factory.setAutoStartup(cfg.getAutoStartup());
        return factory;
    }

    /**
     * 注册 RabbitTemplate。
     *
     * <p>业务方没有声明名为 rabbitTemplate 的 Bean 时使用该默认 Bean，并统一应用 starter 提供的消息转换器。</p>
     *
     * @param connectionFactory RabbitMQ 连接工厂
     * @param messageConverter  消息转换器
     * @return RabbitTemplate 实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    /**
     * 根据配置动态注册交换机 Bean。
     *
     * <p>仅在显式启用 Vanta RabbitMQ 资源声明时执行；每个交换机会使用配置名称加 {@link #EXCHANGE_SUFFIX} 注册到容器。</p>
     *
     * @return Spring Bean 占位对象，实际交换机通过 {@link SpringBeanUtil} 动态注册
     */
    @Bean
    @ConditionalOnProperty(prefix = PropertiesConstants.RABBITMQ, name = PropertiesConstants.ENABLED, havingValue = "true")
    public Object createExchanges() {
        properties.getExchanges().forEach(config -> {
            Exchange exchange = createExchangeByConfig(config);
            SpringBeanUtil.registerBean(config.getName() + EXCHANGE_SUFFIX, exchange);
        });
        return null;
    }

    /**
     * 根据配置动态注册队列和绑定关系 Bean。
     *
     * <p>队列使用配置名称加 {@link #QUEUE_SUFFIX} 注册；绑定使用队列名、交换机名和 {@link #BINDING_SUFFIX} 组合注册。</p>
     *
     * @return Spring Bean 占位对象，实际队列和绑定通过 {@link SpringBeanUtil} 动态注册
     */
    @Bean
    @DependsOn({"createExchanges"})
    @ConditionalOnProperty(prefix = PropertiesConstants.RABBITMQ, name = PropertiesConstants.ENABLED, havingValue = "true")
    public Object createQueuesAndBinding() {
        properties.getQueues().forEach(queueConfig -> {
            Queue queue = new Queue(
                    queueConfig.getName(),
                    queueConfig.getDurable(),
                    queueConfig.getExclusive(),
                    queueConfig.getAutoDelete(),
                    queueConfig.getArgs()
            );
            SpringBeanUtil.registerBean(queueConfig.getName() + QUEUE_SUFFIX, queue);

            String exchangeName = queueConfig.getExchangeName();
            if (exchangeName == null || exchangeName.isBlank()) {
                return;
            }

            Arrays
                    .stream(exchangeName.split(StringConstants.COMMA))
                    .map(String::trim)
                    .forEach(name -> {
                        var opt = properties.getExchanges()
                                .stream()
                                .filter(e -> e.getName().equals(name))
                                .findFirst();

                        opt.ifPresent(exchangeConfig -> {
                            Exchange exchange = SpringBeanUtil.getBean(exchangeConfig.getName() + EXCHANGE_SUFFIX);
                            Binding binding = createBinding(queue, exchange, queueConfig);
                            if (binding != null) {
                                SpringBeanUtil.registerBean(queueConfig.getName() + exchangeConfig.getName() + BINDING_SUFFIX, binding);
                            }
                        });
                    });
        });
        return null;
    }

    /**
     * 将交换机配置转换为 Spring AMQP 交换机对象。
     *
     * @param config 交换机配置
     * @return Spring AMQP 交换机对象
     */
    private Exchange createExchangeByConfig(RabbitMqProperties.ExchangeConfig config) {
        return switch (config.getType()) {
            case DIRECT -> new DirectExchange(
                    config.getName(),
                    config.getDurable() != null ? config.getDurable() : true,
                    config.getAutoDelete() != null ? config.getAutoDelete() : false,
                    config.getArguments()
            );
            case TOPIC -> new TopicExchange(
                    config.getName(),
                    config.getDurable() != null ? config.getDurable() : true,
                    config.getAutoDelete() != null ? config.getAutoDelete() : false,
                    config.getArguments()
            );
            case FANOUT -> new FanoutExchange(
                    config.getName(),
                    config.getDurable() != null ? config.getDurable() : true,
                    config.getAutoDelete() != null ? config.getAutoDelete() : false,
                    config.getArguments()
            );
            case HEADERS -> new HeadersExchange(
                    config.getName(),
                    config.getDurable() != null ? config.getDurable() : true,
                    config.getAutoDelete() != null ? config.getAutoDelete() : false,
                    config.getArguments()
            );
            case CUSTOM -> new CustomExchange(
                    config.getName(),
                    config.getCustomType(),
                    config.getDurable() != null ? config.getDurable() : true,
                    config.getAutoDelete() != null ? config.getAutoDelete() : false,
                    config.getArguments()
            );
        };
    }

    /**
     * 根据交换机类型创建队列绑定关系。
     *
     * @param queue       队列对象
     * @param exchange    交换机对象
     * @param queueConfig 队列绑定配置
     * @return 支持的交换机类型会返回 Binding，不支持时返回 null
     */
    private Binding createBinding(Queue queue, Exchange exchange, RabbitMqProperties.QueueConfig queueConfig) {

        if (exchange instanceof TopicExchange topicExchange) {
            return BindingBuilder.bind(queue).to(topicExchange).with(queueConfig.getRoutingKey());
        }

        if (exchange instanceof DirectExchange directExchange) {
            return BindingBuilder.bind(queue).to(directExchange).with(queueConfig.getRoutingKey());
        }

        if (exchange instanceof FanoutExchange fanoutExchange) {
            return BindingBuilder.bind(queue).to(fanoutExchange);
        }

        if (exchange instanceof HeadersExchange headersExchange) {
            if (queueConfig.getWhereAll() != null && queueConfig.getWhereAll()) {
                return BindingBuilder.bind(queue).to(headersExchange).whereAll(queueConfig.getHeaders()).match();
            }
            return BindingBuilder.bind(queue).to(headersExchange).whereAny(queueConfig.getHeaders()).match();
        }

        if (exchange instanceof CustomExchange customExchange) {
            return BindingBuilder.bind(queue).to(customExchange).with(queueConfig.getRoutingKey()).noargs();
        }

        return null;
    }

    /**
     * 输出自动配置初始化完成日志。
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'RabbitMq' completed initialization.");
    }

}
