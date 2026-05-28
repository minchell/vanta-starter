package com.vanta.starter.core.util;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * 通用配置文件属性源工厂。
 * <p>
 * Spring 标准 {@link DefaultPropertySourceFactory} 默认不直接解析通过 {@code @PropertySource} 引入的 YAML 文件。
 * 该工厂在资源扩展名为 {@code .yml} 或 {@code .yaml} 时使用 {@link YamlPropertySourceLoader} 读取，
 * 其他格式仍交给父类处理。
 * </p>
 */
public class GeneralPropertySourceFactory extends DefaultPropertySourceFactory {

    /**
     * 根据资源类型创建属性源。
     *
     * @param name            显式属性源名称，允许为空。
     * @param encodedResource 带编码信息的配置资源。
     * @return 解析后的 Spring 属性源。
     * @throws IOException 读取配置资源失败时抛出。
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource encodedResource) throws IOException {
        Resource resource = encodedResource.getResource();
        String resourceName = resource.getFilename();
        if (CharSequenceUtil.isNotBlank(resourceName) && CharSequenceUtil.endWithAny(resourceName, ".yml", ".yaml")) {
            return new YamlPropertySourceLoader().load(resourceName, resource).get(0);
        }
        return super.createPropertySource(name, encodedResource);
    }
}
