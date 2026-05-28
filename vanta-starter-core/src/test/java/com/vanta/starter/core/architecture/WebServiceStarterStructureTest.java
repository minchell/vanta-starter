package com.vanta.starter.core.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class WebServiceStarterStructureTest {

    @Test
    void webServiceStarterShouldProvideThinAutoConfigurationAndDefaultResource() throws IOException {
        Path repoRoot = Path.of("..").toAbsolutePath().normalize();
        Path module = repoRoot.resolve("vanta-starter-web-service");

        assertThat(module.resolve("pom.xml")).exists();
        assertThat(module.resolve("src/main/resources/default-web-service.yml")).exists();
        assertThat(module.resolve("src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")).exists();
        assertThat(module.resolve("src/main/java/com/vanta/starter/webservice/autoconfigure/WebServiceAutoConfiguration.java")).exists();

        String imports = Files.readString(module.resolve("src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"), StandardCharsets.UTF_8);
        assertThat(imports).contains("com.vanta.starter.webservice.autoconfigure.WebServiceAutoConfiguration");

        String defaults = Files.readString(module.resolve("src/main/resources/default-web-service.yml"), StandardCharsets.UTF_8);
        assertThat(defaults)
                .contains("vanta-starter:")
                .contains("web-service:")
                .contains("enabled: true")
                .contains("response:")
                .contains("exception:")
                .contains("validation:")
                .contains("api-doc:")
                .contains("cors:");
    }

    @Test
    void webServiceExampleShouldDocumentTheMinimumServiceSkeleton() {
        Path repoRoot = Path.of("..").toAbsolutePath().normalize();
        Path example = repoRoot.resolve("examples/vanta-web-service-demo");

        assertThat(example.resolve("README.md")).exists();
        assertThat(example.resolve("src/main/java/com/vanta/example/webservice/WebServiceDemoApplication.java")).exists();
        assertThat(example.resolve("src/main/java/com/vanta/example/webservice/controller/HealthController.java")).exists();
        assertThat(example.resolve("src/main/java/com/vanta/example/webservice/controller/DemoController.java")).exists();
        assertThat(example.resolve("src/main/java/com/vanta/example/webservice/service/DemoService.java")).exists();
        assertThat(example.resolve("src/main/java/com/vanta/example/webservice/model/DemoCreateReq.java")).exists();
        assertThat(example.resolve("src/main/resources/application.yml")).exists();
    }
}
