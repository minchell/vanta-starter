package com.vanta.starter.core.lombok;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * vanta-starter 访问器注解治理测试。
 *
 * <p>该测试固定使用 UTF-8 读取源码，确保模块内不再通过 Lombok 的访问器注解生成 getter、setter 或 Data 方法。</p>
 */
class LombokAccessorPolicyTest {

    /**
     * 当前测试模块相对 vanta-starter 根目录的层级数。
     */
    private static final int STARTER_ROOT_PARENT_DEPTH = 1;

    /**
     * 确认 vanta-starter 源码内没有 Lombok 访问器注解。
     *
     * @throws IOException 源码扫描失败时抛出
     */
    @Test
    void shouldNotUseLombokAccessorAnnotationsInVantaStarter() throws IOException {
        Path starterRoot = Path.of("").toAbsolutePath().normalize();
        for (int i = 0; i < STARTER_ROOT_PARENT_DEPTH; i++) {
            starterRoot = starterRoot.getParent();
        }

        List<String> violations;
        try (Stream<Path> paths = Files.walk(starterRoot)) {
            violations = paths
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> path.toString().contains("\\src\\main\\java\\")
                            || path.toString().contains("/src/main/java/"))
                    .filter(this::containsLombokAccessorAnnotation)
                    .map(starterRoot::relativize)
                    .map(Path::toString)
                    .sorted()
                    .toList();
        }

        assertTrue(
                violations.isEmpty(),
                () -> "vanta-starter 仍存在 Lombok 访问器注解，请改为显式方法：%s".formatted(violations)
        );
    }

    /**
     * 判断源码文件是否包含 Lombok 访问器注解或对应导入。
     *
     * @param path 源码文件路径
     * @return 包含违规访问器注解时返回 true
     */
    private boolean containsLombokAccessorAnnotation(Path path) {
        try {
            String source = Files.readString(path, StandardCharsets.UTF_8);
            return source.contains("import lombok.Getter;")
                    || source.contains("import lombok.Setter;")
                    || source.contains("import lombok.Data;")
                    || source.contains("@Getter")
                    || source.contains("@Setter")
                    || source.contains("@Data");
        } catch (IOException e) {
            throw new IllegalStateException("读取源码文件失败：" + path, e);
        }
    }
}
