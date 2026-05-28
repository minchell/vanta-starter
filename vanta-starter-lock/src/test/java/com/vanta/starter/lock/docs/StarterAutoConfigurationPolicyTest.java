package com.vanta.starter.lock.docs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * starter 自动配置策略测试。
 *
 * <p>该测试从源码层面约束普通 starter 的能力开关：凡是使用 enabled=true 作为能力入口的自动配置，
 * 都必须由业务项目显式开启，不能在只引入 Maven 依赖时默认生效。Web 服务基础架构组合模块是内部新项目
 * 的最小服务底座，允许默认开启。</p>
 */
class StarterAutoConfigurationPolicyTest {

    /**
     * starter 根目录。
     */
    private static final Path STARTER_ROOT = resolveStarterRoot();

    /**
     * 自动配置条件注解匹配表达式。
     */
    private static final Pattern CONDITIONAL_ON_PROPERTY_PATTERN =
            Pattern.compile("@ConditionalOnProperty\\(([^)]*)\\)", Pattern.DOTALL);

    /**
     * 解析 starter 根目录。
     *
     * @return starter 根目录路径
     */
    private static Path resolveStarterRoot() {
        Path current = Path.of("").toAbsolutePath();
        while (current != null) {
            Path candidate = current.resolve("vanta-starter");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
            if (current.getFileName() != null && current.getFileName().toString().equals("vanta-starter")) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Cannot locate vanta-starter directory");
    }

    /**
     * enabled=true 能力开关不允许默认启用。
     *
     * <p>type=local、cache.type=default 这类子策略默认值可以存在，因为它们应当被外层 enabled=true
     * 包住；真正的能力入口必须遵守显式开启原则。</p>
     *
     * @throws IOException 遍历源码失败时抛出
     */
    @Test
    void shouldNotEnableStarterCapabilitiesByDefault() throws IOException {
        List<String> violations = new ArrayList<>();

        for (Path javaFile : findStarterJavaFiles()) {
            if (isWebServiceBaseline(javaFile)) {
                continue;
            }
            String source = Files.readString(javaFile, StandardCharsets.UTF_8);
            Matcher matcher = CONDITIONAL_ON_PROPERTY_PATTERN.matcher(source);
            while (matcher.find()) {
                String annotationBody = matcher.group(1);
                if (isEnabledSwitch(annotationBody) && hasMatchIfMissingTrue(annotationBody)) {
                    violations.add("%s enables starter capability by default: %s"
                            .formatted(STARTER_ROOT.relativize(javaFile), compact(annotationBody)));
                }
            }
        }

        assertThat(violations).isEmpty();
    }

    /**
     * 判断是否为允许默认启用的 Web 服务基础架构组合模块。
     *
     * @param javaFile Java 源码文件
     * @return Web 服务基础架构组合模块时返回 true
     */
    private boolean isWebServiceBaseline(Path javaFile) {
        return javaFile.toString().contains("vanta-starter-web-service");
    }

    /**
     * 判断注解是否使用 enabled 作为能力开关。
     *
     * @param annotationBody 注解参数正文
     * @return 使用 enabled 作为开关时返回 true
     */
    private boolean isEnabledSwitch(String annotationBody) {
        return annotationBody.contains("name = PropertiesConstants.ENABLED")
                || annotationBody.contains("name=PropertiesConstants.ENABLED")
                || annotationBody.contains("name = \"enabled\"")
                || annotationBody.contains("name=\"enabled\"");
    }

    /**
     * 判断注解是否配置了 matchIfMissing=true。
     *
     * @param annotationBody 注解参数正文
     * @return 缺省匹配 true 时返回 true
     */
    private boolean hasMatchIfMissingTrue(String annotationBody) {
        return annotationBody.contains("matchIfMissing = true")
                || annotationBody.contains("matchIfMissing=true");
    }

    /**
     * 压缩注解正文，便于断言失败时阅读。
     *
     * @param annotationBody 注解参数正文
     * @return 单行注解摘要
     */
    private String compact(String annotationBody) {
        return annotationBody.replaceAll("\\s+", " ").trim();
    }

    /**
     * 查找 starter 下所有 Java 源码文件。
     *
     * @return Java 源码路径列表
     * @throws IOException 遍历失败时抛出
     */
    private List<Path> findStarterJavaFiles() throws IOException {
        try (var stream = Files.walk(STARTER_ROOT)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.toString().contains("target"))
                    .toList();
        }
    }
}
