package com.vanta.starter.core.architecture;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StarterRepositoryStructureTest {

    @Test
    void rootPomShouldUseDependenciesAsParentAndExposeRootBomModule() throws Exception {
        Path repoRoot = Path.of("..").toAbsolutePath().normalize();
        Document rootPom = readPom(repoRoot.resolve("pom.xml"));

        assertThat(text(rootPom, "/*[local-name()='project']/*[local-name()='parent']/*[local-name()='groupId']")).isEqualTo("com.vanta");
        assertThat(text(rootPom, "/*[local-name()='project']/*[local-name()='parent']/*[local-name()='artifactId']")).isEqualTo("vanta-starter-dependencies");
        assertThat(text(rootPom, "/*[local-name()='project']/*[local-name()='parent']/*[local-name()='relativePath']")).isEqualTo("./vanta-starter-dependencies/pom.xml");

        List<String> modules = texts(rootPom, "/*[local-name()='project']/*[local-name()='modules']/*[local-name()='module']");
        assertThat(modules).contains("vanta-starter-dependencies", "vanta-starter-bom", "vanta-starter-core", "vanta-starter-web-service", "examples/vanta-web-service-demo");
        assertThat(modules).doesNotContain("vanta-starter-parent");
    }

    @Test
    void dependenciesPomShouldImportBomAndNotAggregateChildModules() throws Exception {
        Path repoRoot = Path.of("..").toAbsolutePath().normalize();
        Document dependenciesPom = readPom(repoRoot.resolve("vanta-starter-dependencies/pom.xml"));

        assertThat(count(dependenciesPom, "/*[local-name()='project']/*[local-name()='parent']")).isZero();
        assertThat(text(dependenciesPom, "/*[local-name()='project']/*[local-name()='groupId']")).isEqualTo("com.vanta");
        assertThat(text(dependenciesPom, "/*[local-name()='project']/*[local-name()='artifactId']")).isEqualTo("vanta-starter-dependencies");
        assertThat(text(dependenciesPom, "/*[local-name()='project']/*[local-name()='version']")).isEqualTo("${revision}");
        assertThat(count(dependenciesPom, "/*[local-name()='project']/*[local-name()='modules']/*[local-name()='module']")).isZero();
        assertThat(texts(dependenciesPom, "/*[local-name()='project']/*[local-name()='dependencyManagement']/*[local-name()='dependencies']/*[local-name()='dependency']/*[local-name()='artifactId']"))
                .contains("vanta-starter-bom");
    }

    @Test
    void bomPomShouldBeRootLevelAndSelfContained() throws Exception {
        Path repoRoot = Path.of("..").toAbsolutePath().normalize();
        Document bomPom = readPom(repoRoot.resolve("vanta-starter-bom/pom.xml"));

        assertThat(count(bomPom, "/*[local-name()='project']/*[local-name()='parent']")).isZero();
        assertThat(text(bomPom, "/*[local-name()='project']/*[local-name()='groupId']")).isEqualTo("com.vanta");
        assertThat(text(bomPom, "/*[local-name()='project']/*[local-name()='artifactId']")).isEqualTo("vanta-starter-bom");
        assertThat(text(bomPom, "/*[local-name()='project']/*[local-name()='version']")).isEqualTo("${revision}");
        assertThat(text(bomPom, "/*[local-name()='project']/*[local-name()='properties']/*[local-name()='spring-boot.version']")).isNotBlank();
        assertThat(texts(bomPom, "/*[local-name()='project']/*[local-name()='dependencyManagement']/*[local-name()='dependencies']/*[local-name()='dependency']/*[local-name()='artifactId']"))
                .contains("spring-boot-dependencies", "vanta-starter-core");
    }

    @Test
    void topLevelModulesShouldInheritFromRepositoryRootPom() throws Exception {
        Path repoRoot = Path.of("..").toAbsolutePath().normalize();
        List<Path> topLevelModulePoms = List.of(
                repoRoot.resolve("vanta-starter-core/pom.xml"),
                repoRoot.resolve("vanta-starter-jackson/pom.xml"),
                repoRoot.resolve("vanta-starter-api-doc/pom.xml"),
                repoRoot.resolve("vanta-starter-validation/pom.xml"),
                repoRoot.resolve("vanta-starter-web-service/pom.xml"),
                repoRoot.resolve("vanta-starter-web/pom.xml"),
                repoRoot.resolve("vanta-starter-trace/pom.xml"),
                repoRoot.resolve("vanta-starter-data/pom.xml"),
                repoRoot.resolve("vanta-starter-auth/pom.xml"),
                repoRoot.resolve("vanta-starter-cache/pom.xml"),
                repoRoot.resolve("vanta-starter-encrypt/pom.xml"),
                repoRoot.resolve("vanta-starter-excel/pom.xml"),
                repoRoot.resolve("vanta-starter-log/pom.xml"),
                repoRoot.resolve("vanta-starter-security/pom.xml"),
                repoRoot.resolve("vanta-starter-idempotent/pom.xml"),
                repoRoot.resolve("vanta-starter-ratelimiter/pom.xml"),
                repoRoot.resolve("vanta-starter-messaging/pom.xml"),
                repoRoot.resolve("vanta-starter-influxdb/pom.xml"),
                repoRoot.resolve("vanta-starter-zookeeper/pom.xml"),
                repoRoot.resolve("vanta-starter-nacos/pom.xml"),
                repoRoot.resolve("vanta-starter-elasticsearch/pom.xml"),
                repoRoot.resolve("vanta-starter-observability/pom.xml"),
                repoRoot.resolve("vanta-starter-lock/pom.xml"),
                repoRoot.resolve("vanta-starter-storage/pom.xml"),
                repoRoot.resolve("vanta-starter-scheduler/pom.xml")
        );

        List<String> violations = new ArrayList<>();
        for (Path pom : topLevelModulePoms) {
            Document modulePom = readPom(pom);
            String artifactId = text(modulePom, "/*[local-name()='project']/*[local-name()='artifactId']");
            String parentArtifactId = text(modulePom, "/*[local-name()='project']/*[local-name()='parent']/*[local-name()='artifactId']");
            String relativePath = text(modulePom, "/*[local-name()='project']/*[local-name()='parent']/*[local-name()='relativePath']");
            if (!"vanta-starter".equals(parentArtifactId) || !"../pom.xml".equals(relativePath)) {
                violations.add(artifactId + " -> " + parentArtifactId + " @ " + relativePath);
            }
        }

        assertThat(violations).isEmpty();
    }

    @Test
    void bomPomShouldManageWebServiceStarterAndExampleShouldUseItAsOnlyVantaDependency() throws Exception {
        Path repoRoot = Path.of("..").toAbsolutePath().normalize();
        Document bomPom = readPom(repoRoot.resolve("vanta-starter-bom/pom.xml"));
        Document examplePom = readPom(repoRoot.resolve("examples/vanta-web-service-demo/pom.xml"));

        assertThat(texts(bomPom, "/*[local-name()='project']/*[local-name()='dependencyManagement']/*[local-name()='dependencies']/*[local-name()='dependency']/*[local-name()='artifactId']"))
                .contains("vanta-starter-web-service");

        List<String> exampleVantaDependencies = texts(examplePom,
                "/*[local-name()='project']/*[local-name()='dependencies']/*[local-name()='dependency'][*[local-name()='groupId']='com.vanta']/*[local-name()='artifactId']");
        assertThat(exampleVantaDependencies).containsExactly("vanta-starter-web-service");
    }

    private static Document readPom(Path pom) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(pom.toFile());
    }

    private static String text(Document document, String path) {
        NodeList nodes = nodes(document, path);
        return nodes.getLength() == 0 ? null : nodes.item(0).getTextContent().trim();
    }

    private static List<String> texts(Document document, String path) {
        NodeList nodes = nodes(document, path);
        List<String> values = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            values.add(nodes.item(i).getTextContent().trim());
        }
        return values;
    }

    private static int count(Document document, String path) {
        return nodes(document, path).getLength();
    }

    private static NodeList nodes(Document document, String path) {
        try {
            return (NodeList) XPathFactory.newInstance().newXPath().evaluate(path, document, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to evaluate XPath: " + path, e);
        }
    }
}
