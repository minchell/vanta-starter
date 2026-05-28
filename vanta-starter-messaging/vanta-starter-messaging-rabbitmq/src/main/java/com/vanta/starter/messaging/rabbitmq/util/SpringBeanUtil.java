package com.vanta.starter.messaging.rabbitmq.util;

import cn.hutool.core.exceptions.UtilException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * Spring Bean 动态注册工具。
 *
 * <p>RabbitMQ starter 会根据配置动态创建交换机、队列和绑定 Bean。该工具保存 Spring 容器引用，
 * 并提供按名称获取、按类型获取、动态注册和动态注销单例 Bean 的能力。</p>
 */
public class SpringBeanUtil implements BeanFactoryPostProcessor, ApplicationContextAware {

    /**
     * Spring 应用上下文。
     *
     * <p>当 {@link #beanFactory} 尚未注入时，用它兜底获取 BeanFactory。</p>
     */
    private static ApplicationContext applicationContext;

    /**
     * 可配置 BeanFactory。
     *
     * <p>动态注册交换机、队列和绑定 Bean 时需要使用该对象。</p>
     */
    private static ConfigurableListableBeanFactory beanFactory;

    /**
     * 获取当前可用的 BeanFactory。
     *
     * @return 当前 Spring 容器的 BeanFactory 或 ApplicationContext
     */
    public static ListableBeanFactory getBeanFactory() {
        final ListableBeanFactory factory = null == beanFactory ? applicationContext : beanFactory;
        if (null == factory) {
            throw new UtilException("No ConfigurableListableBeanFactory or ApplicationContext injected, maybe not in the Spring environment?");
        }
        return factory;
    }

    /**
     * 按 Bean 名称获取对象。
     *
     * @param name Bean 名称
     * @param <T>  Bean 类型
     * @return Bean 实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getBeanFactory().getBean(name);
    }

    /**
     * 按类型获取 Bean。
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getBeanFactory().getBean(clazz);
    }

    /**
     * 按名称和类型获取 Bean。
     *
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @param <T>   Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getBeanFactory().getBean(name, clazz);
    }

    /**
     * 获取指定类型对应的所有Bean，包括子类
     *
     * @param <T>  Bean类型
     * @param type 类、接口，null表示获取所有bean
     * @return 类型对应的bean，key是bean注册的name，value是Bean
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    /**
     * 获取可配置 BeanFactory。
     *
     * @return 可配置 BeanFactory
     * @throws UtilException 当前环境无法提供 ConfigurableListableBeanFactory 时抛出
     */
    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() throws UtilException {
        final ConfigurableListableBeanFactory factory;
        if (null != beanFactory) {
            factory = beanFactory;
        } else if (applicationContext instanceof ConfigurableApplicationContext) {
            factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        } else {
            throw new UtilException("No ConfigurableListableBeanFactory from context!");
        }
        return factory;
    }

    /**
     * 动态注册单例 Bean。
     *
     * <p>RabbitMQ starter 使用该方法把配置生成的交换机、队列和绑定注册进容器。</p>
     *
     * @param beanName Bean 名称
     * @param bean     Bean 实例
     * @param <T>      Bean 类型
     */
    public static <T> void registerBean(String beanName, T bean) {
        final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        factory.autowireBean(bean);
        factory.registerSingleton(beanName, bean);
    }

    /**
     * 动态注销单例 Bean。
     *
     * @param beanName Bean 名称
     */
    public static void unregisterBean(String beanName) {
        final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        if (factory instanceof DefaultSingletonBeanRegistry registry) {
            registry.destroySingleton(beanName);
        } else {
            throw new UtilException("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
        }
    }

    /**
     * 注入 Spring 应用上下文。
     *
     * @param applicationContext Spring 应用上下文
     * @throws BeansException Spring 回调异常
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtil.applicationContext = applicationContext;
    }

    /**
     * 注入可配置 BeanFactory。
     *
     * @param beanFactory 可配置 BeanFactory
     * @throws BeansException Spring 回调异常
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringBeanUtil.beanFactory = beanFactory;
    }

}
