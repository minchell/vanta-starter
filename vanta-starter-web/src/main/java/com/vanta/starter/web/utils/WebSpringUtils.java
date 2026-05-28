package com.vanta.starter.web.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.vanta.starter.core.constant.StringConstants;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import java.util.Map;

/**
 * Spring Web 与 Spring MVC 工具类。
 * <p>
 * 该类承接原本混在 starter-core 中的 Web 容器能力，包括静态资源映射动态注册、
 * 静态资源映射注销以及根据当前请求解析 Controller HandlerMethod。它依赖 Servlet
 * 和 Spring MVC，因此必须归属于 {@code vanta-starter-web}。
 * </p>
 */
public class WebSpringUtils {

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private WebSpringUtils() {
    }

    /**
     * 注销已经注册的静态资源映射。
     *
     * @param handlerMap 静态资源映射，key 为访问路径，value 为本地资源目录。
     */
    @SuppressWarnings("unchecked")
    public static void deRegisterResourceHandler(Map<String, String> handlerMap) {
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();

        HandlerMapping resourceHandlerMapping = applicationContext.getBean("resourceHandlerMapping", HandlerMapping.class);

        Map<String, Object> oldHandlerMap = (Map<String, Object>) ReflectUtil.getFieldValue(resourceHandlerMapping, "handlerMap");

        for (Map.Entry<String, String> entry : handlerMap.entrySet()) {
            String pathPattern = CharSequenceUtil.appendIfMissing(entry.getKey(), StringConstants.PATH_PATTERN);
            oldHandlerMap.remove(pathPattern);
        }
    }

    /**
     * 注册或刷新静态资源映射。
     * <p>
     * 调用方传入的访问路径会统一补齐为 {@code /**} 结尾，资源目录会统一补齐为 {@code /} 结尾。
     * 该方法会先移除同路径旧映射，再把新映射注册到 Spring MVC 资源处理器。
     * </p>
     *
     * @param handlerMap 静态资源映射，key 为访问路径，value 为本地资源目录。
     */
    @SuppressWarnings("unchecked")
    public static void registerResourceHandler(Map<String, String> handlerMap) {
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();

        HandlerMapping resourceHandlerMapping = applicationContext.getBean("resourceHandlerMapping", HandlerMapping.class);

        Map<String, Object> oldHandlerMap = (Map<String, Object>) ReflectUtil.getFieldValue(resourceHandlerMapping, "handlerMap");

        ServletContext servletContext = applicationContext.getBean(ServletContext.class);
        ContentNegotiationManager contentNegotiationManager = applicationContext.getBean("mvcContentNegotiationManager", ContentNegotiationManager.class);
        UrlPathHelper urlPathHelper = applicationContext.getBean("mvcUrlPathHelper", UrlPathHelper.class);

        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(
                applicationContext,
                servletContext,
                contentNegotiationManager,
                urlPathHelper
        );

        for (Map.Entry<String, String> entry : handlerMap.entrySet()) {
            String pathPattern = CharSequenceUtil.appendIfMissing(
                    CharSequenceUtil.removeSuffix(entry.getKey(), StringConstants.SLASH),
                    StringConstants.PATH_PATTERN
            );
            oldHandlerMap.remove(pathPattern);
            String resourceLocations = CharSequenceUtil.appendIfMissing(entry.getValue(), StringConstants.SLASH);
            registry.addResourceHandler(pathPattern).addResourceLocations("file:" + resourceLocations);
        }

        Map<String, ?> additionalUrlMap = ReflectUtil.<SimpleUrlHandlerMapping>invoke(registry, "getHandlerMapping").getUrlMap();
        ReflectUtil.<Void>invoke(resourceHandlerMapping, "registerHandlers", additionalUrlMap);
    }

    /**
     * 根据当前 HTTP 请求解析对应的 Controller 方法。
     *
     * @param request 当前 HTTP 请求。
     * @return 匹配到的 Controller 方法；无法解析或非 Controller 请求时返回 {@code null}。
     */
    public static HandlerMethod getHandlerMethod(HttpServletRequest request) {
        try {
            RequestMappingHandlerMapping handlerMapping = SpringUtil.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
            if (handlerExecutionChain == null) {
                return null;
            }
            Object handler = handlerExecutionChain.getHandler();
            if (handler instanceof HandlerMethod handlerMethod) {
                return handlerMethod;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
