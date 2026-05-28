package com.vanta.starter.core.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class StarterAutoConfigurationStructureTest {

    private static final Path REPO_ROOT = Path.of("..").toAbsolutePath().normalize();
    private static final Path IMPORTS_RELATIVE_PATH = Path.of(
            "src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("(?m)^package\\s+([\\w.]+);");
    private static final Pattern CLASS_PATTERN = Pattern.compile("(?m)^public\\s+class\\s+(\\w+)");

    @Test
    void autoConfigurationClassesShouldBeDeclaredInModuleImports() throws IOException {
        List<String> violations = new ArrayList<>();

        for (Path javaFile : findAutoConfigurationFiles()) {
            Path moduleRoot = findModuleRoot(javaFile);
            String autoConfigurationClass = fullyQualifiedClassName(javaFile);
            Path importsFile = moduleRoot.resolve(IMPORTS_RELATIVE_PATH);

            if (!Files.exists(importsFile)) {
                violations.add("%s misses %s for %s".formatted(
                        REPO_ROOT.relativize(moduleRoot), IMPORTS_RELATIVE_PATH, autoConfigurationClass));
                continue;
            }

            String imports = Files.readString(importsFile, StandardCharsets.UTF_8);
            if (!imports.contains(autoConfigurationClass)) {
                violations.add("%s does not import %s".formatted(
                        REPO_ROOT.relativize(importsFile), autoConfigurationClass));
            }
        }

        assertThat(violations).isEmpty();
    }

    @Test
    void autoConfigurationClassesShouldHaveUniqueFullyQualifiedNames() throws IOException {
        Map<String, List<Path>> locationsByClassName = new LinkedHashMap<>();

        for (Path javaFile : findAutoConfigurationFiles()) {
            locationsByClassName
                    .computeIfAbsent(fullyQualifiedClassName(javaFile), key -> new ArrayList<>())
                    .add(REPO_ROOT.relativize(javaFile));
        }

        Map<String, List<Path>> duplicates = new LinkedHashMap<>();
        locationsByClassName.forEach((className, locations) -> {
            if (locations.size() > 1) {
                duplicates.put(className, locations);
            }
        });

        assertThat(duplicates).isEmpty();
    }

    @Test
    void starterAutoConfigurationsShouldNotEnableWebMvcDirectly() throws IOException {
        List<String> violations = new ArrayList<>();

        for (Path javaFile : findAutoConfigurationFiles()) {
            String source = Files.readString(javaFile, StandardCharsets.UTF_8);
            if (source.contains("@EnableWebMvc")) {
                violations.add(REPO_ROOT.relativize(javaFile).toString());
            }
        }

        assertThat(violations).isEmpty();
    }

    @Test
    void importedAutoConfigurationClassesShouldUseAutoConfigurationAnnotation() throws IOException {
        List<String> violations = new ArrayList<>();

        for (Path javaFile : findAutoConfigurationFiles()) {
            String source = Files.readString(javaFile, StandardCharsets.UTF_8);
            String autoConfigurationClass = fullyQualifiedClassName(javaFile);
            if (!source.contains("@AutoConfiguration")) {
                violations.add("%s should use @AutoConfiguration for %s".formatted(
                        REPO_ROOT.relativize(javaFile), autoConfigurationClass));
            }
        }

        assertThat(violations).isEmpty();
    }

    @Test
    void remoteOrCrossCuttingStartersShouldRequireExplicitEnabledProperty() throws IOException {
        assertClassLevelEnabledGuard(
                "vanta-starter-messaging/vanta-starter-messaging-rabbitmq/src/main/java/com/vanta/starter/messaging/rabbitmq/autoconfigure/RabbitMqAutoConfiguration.java",
                "RabbitMqAutoConfiguration",
                "PropertiesConstants.RABBITMQ"
        );
        assertClassLevelEnabledGuard(
                "vanta-starter-security/vanta-starter-security-sensitivewords/src/main/java/com/vanta/starter/security/sensitivewords/autoconfigure/SensitiveWordsAutoConfiguration.java",
                "SensitiveWordsAutoConfiguration",
                "PropertiesConstants.SECURITY_SENSITIVE_WORDS"
        );
        assertClassLevelEnabledGuard(
                "vanta-starter-cache/vanta-starter-cache-springcache/src/main/java/com/vanta/starter/cache/springcache/autoconfigure/SpringCacheAutoConfiguration.java",
                "SpringCacheAutoConfiguration",
                "PropertiesConstants.CACHE_SPRING_CACHE"
        );
        assertClassLevelEnabledGuard(
                "vanta-starter-cache/vanta-starter-cache-jetcache/src/main/java/com/vanta/starter/cache/jetcache/autoconfigure/JetCacheAutoConfiguration.java",
                "JetCacheAutoConfiguration",
                "PropertiesConstants.CACHE_JETCACHE"
        );
    }

    @Test
    void optionalStarterEnabledPropertiesShouldDefaultToFalse() throws IOException {
        List<String> violations = new ArrayList<>();
        List<String> optionalEnabledProperties = List.of(
                "vanta-starter-auth/vanta-starter-auth-justauth/src/main/java/com/vanta/starter/auth/justauth/autoconfigure/JustAuthProperties.java",
                "vanta-starter-cache/vanta-starter-cache-redis/src/main/java/com/vanta/starter/cache/redis/autoconfigure/RedissonProperties.java",
                "vanta-starter-log/vanta-starter-log-core/src/main/java/com/vanta/starter/log/model/LogProperties.java",
                "vanta-starter-ratelimiter/src/main/java/com/vanta/starter/ratelimiter/autoconfigure/RateLimiterProperties.java",
                "vanta-starter-security/vanta-starter-security-xss/src/main/java/com/vanta/starter/security/xss/autoconfigure/XssProperties.java"
        );

        for (String relativePath : optionalEnabledProperties) {
            Path javaFile = REPO_ROOT.resolve(relativePath);
            String source = Files.readString(javaFile, StandardCharsets.UTF_8);
            if (!source.contains("private boolean enabled = false;")) {
                violations.add(relativePath + " should default enabled=false");
            }
        }

        assertThat(violations).isEmpty();
    }

    private static void assertClassLevelEnabledGuard(String relativePath, String className, String prefix) throws IOException {
        Path javaFile = REPO_ROOT.resolve(relativePath);
        String source = Files.readString(javaFile, StandardCharsets.UTF_8);
        String beforeClassDeclaration = source.substring(0, source.indexOf("public class " + className));

        assertThat(beforeClassDeclaration)
                .contains("@ConditionalOnProperty(prefix = " + prefix
                        + ", name = PropertiesConstants.ENABLED, havingValue = \"true\")");
    }

    private static List<Path> findAutoConfigurationFiles() throws IOException {
        try (var stream = Files.walk(REPO_ROOT)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().contains("src\\main\\java")
                            || path.toString().contains("src/main/java"))
                    .filter(path -> path.getFileName().toString().endsWith("AutoConfiguration.java"))
                    .filter(path -> !path.toString().contains("target"))
                    .toList();
        }
    }

    private static Path findModuleRoot(Path javaFile) {
        Path current = javaFile.getParent();
        while (current != null) {
            if (Files.exists(current.resolve("pom.xml")) && Files.isDirectory(current.resolve("src/main/java"))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Cannot locate module root for " + javaFile);
    }

    private static String fullyQualifiedClassName(Path javaFile) throws IOException {
        String source = Files.readString(javaFile, StandardCharsets.UTF_8);
        return packageName(source) + "." + className(source);
    }

    private static String packageName(String source) {
        Matcher matcher = PACKAGE_PATTERN.matcher(source);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    private static String className(String source) {
        Matcher matcher = CLASS_PATTERN.matcher(source);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }
}
