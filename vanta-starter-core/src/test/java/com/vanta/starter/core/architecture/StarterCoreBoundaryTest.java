package com.vanta.starter.core.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StarterCoreBoundaryTest {

    private static void collectSourceViolations(Path root,
                                                Path sourceFile,
                                                List<String> forbiddenTokens,
                                                List<String> violations) {
        try {
            String content = Files.readString(sourceFile, StandardCharsets.UTF_8);
            for (String token : forbiddenTokens) {
                if (content.contains(token)) {
                    violations.add("%s contains %s".formatted(root.relativize(sourceFile), token));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to scan source file: " + sourceFile, e);
        }
    }

    @Test
    void starterCoreShouldNotContainWebOrServletCode() throws IOException {
        Path mainJava = Path.of(System.getProperty("user.dir"), "src", "main", "java");
        List<String> forbiddenTokens = List.of(
                "jakarta.servlet",
                "org.springframework.web",
                "org.springframework.http",
                "MultipartFile",
                "HttpServletRequest",
                "HttpServletResponse",
                "ServletContext"
        );

        List<String> violations = new ArrayList<>();
        try (Stream<Path> files = Files.walk(mainJava)) {
            files.filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> collectSourceViolations(mainJava, path, forbiddenTokens, violations));
        }

        assertThat(violations)
                .as("vanta-starter-core must stay independent from Servlet/Spring Web APIs; move these capabilities to vanta-starter-web")
                .isEmpty();
    }

    @Test
    void starterCorePomShouldNotDeclareWebDependencies() throws IOException {
        Path pom = Path.of(System.getProperty("user.dir"), "pom.xml");
        String content = Files.readString(pom, StandardCharsets.UTF_8);

        assertThat(content)
                .as("vanta-starter-core pom must not declare Web-only dependencies")
                .doesNotContain("<artifactId>spring-web</artifactId>")
                .doesNotContain("<artifactId>spring-webmvc</artifactId>")
                .doesNotContain("<artifactId>jakarta.servlet-api</artifactId>")
                .doesNotContain("<artifactId>commons-fileupload</artifactId>");
    }
}
