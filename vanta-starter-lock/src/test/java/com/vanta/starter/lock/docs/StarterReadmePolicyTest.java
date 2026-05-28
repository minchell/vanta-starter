package com.vanta.starter.lock.docs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * starter README 文档质量测试。
 *
 * <p>该测试只约束可以长期执行的文档底线：模块 README 必须说明真实作用、
 * 适用场景和接入方式，并保留 Maven 依赖示例。</p>
 */
class StarterReadmePolicyTest {

    private static final Path LOCK_ROOT = resolveLockRoot();

    private static Path resolveLockRoot() {
        Path current = Path.of("").toAbsolutePath();
        while (current != null) {
            Path candidate = current.resolve("vanta-starter").resolve("vanta-starter-lock");
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
            if (current.getFileName() != null
                    && current.getFileName().toString().equals("vanta-starter-lock")) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Cannot locate vanta-starter-lock directory");
    }

    @Test
    void shouldKeepLockReadmeConcreteAndIntegrationFocused() throws IOException {
        Path readme = LOCK_ROOT.resolve("Readme.md");
        String content = Files.readString(readme, StandardCharsets.UTF_8);

        assertThat(content).contains("# vanta-starter-lock");
        assertThat(content).contains("## 1. \u7ec4\u4ef6\u4f5c\u7528");
        assertThat(content).contains("## 2. \u9002\u7528\u573a\u666f");
        assertThat(content).contains("## 3. \u63a5\u5165\u65b9\u5f0f");
        assertThat(content).contains("<artifactId>vanta-starter-lock</artifactId>");
        assertThat(content).doesNotContain("SpringApplication.run");
        assertThat(content).doesNotContain("DemoApplication");
    }
}
