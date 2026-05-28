package com.vanta.starter.core.util;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

import java.util.Map;

/**
 * 模板渲染工具类。
 * <p>
 * 该工具类基于 Hutool 模板引擎从 classpath 加载模板，并用传入参数渲染文本内容。
 * 默认模板根目录为 {@code templates}，不会访问远程模板地址。
 * </p>
 */
public class TemplateUtils {

    /**
     * 默认模板父目录。
     */
    private static final String DEFAULT_TEMPLATE_PARENT_PATH = "templates";

    /**
     * 私有构造方法。
     * <p>
     * 工具类只提供静态方法，不允许被实例化。
     * </p>
     */
    private TemplateUtils() {
    }

    /**
     * 使用默认模板父目录渲染模板。
     *
     * @param templatePath 模板路径，相对于默认 {@code templates} 目录。
     * @param bindingMap   绑定参数，此 Map 中的参数会替换模板中的变量。
     * @return 渲染后的文本内容。
     */
    public static String render(String templatePath, Map<?, ?> bindingMap) {
        return render(DEFAULT_TEMPLATE_PARENT_PATH, templatePath, bindingMap);
    }

    /**
     * 使用指定模板父目录渲染模板。
     *
     * @param parentPath   模板父目录。
     * @param templatePath 模板路径。
     * @param bindingMap   绑定参数，此 Map 中的参数会替换模板中的变量。
     * @return 渲染后的文本内容。
     */
    public static String render(String parentPath, String templatePath, Map<?, ?> bindingMap) {
        TemplateEngine engine = TemplateUtil
                .createEngine(new TemplateConfig(parentPath, TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate(templatePath);
        return template.render(bindingMap);
    }
}
