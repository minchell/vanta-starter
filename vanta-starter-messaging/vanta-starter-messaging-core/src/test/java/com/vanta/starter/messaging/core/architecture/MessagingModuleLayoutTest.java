package com.vanta.starter.messaging.core.architecture;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 消息 starter 模块布局契约测试。
 *
 * <p>该测试约束 RabbitMQ、RocketMQ、Kafka 都归属于统一的 messaging 聚合模块，
 * 避免消息中间件能力分散在 starter 根目录下形成两套入口。
 */
class MessagingModuleLayoutTest {

    /**
     * 从 POM 文件读取直接声明的 module 列表。
     *
     * @param pomPath POM 文件路径。
     * @return POM 内的 module 文本列表。
     * @throws Exception XML 解析失败时抛出。
     */
    private static List<String> readPomModules(Path pomPath) throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        var document = factory.newDocumentBuilder().parse(pomPath.toFile());
        var nodes = document.getElementsByTagName("module");
        var modules = new java.util.ArrayList<String>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            modules.add(((Element) nodes.item(i)).getTextContent().trim());
        }
        return modules;
    }

    /**
     * 从当前测试工作目录向上定位仓库根目录。
     *
     * @return 仓库根目录。
     */
    private static Path locateRepoRoot() {
        Path current = Path.of("").toAbsolutePath().normalize();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("pom.xml"))
                    && Files.isDirectory(current.resolve("vanta-starter-messaging"))
                    && Files.isDirectory(current.resolve("vanta-starter-core"))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("无法定位当前仓库根目录");
    }

    /**
     * 校验 RabbitMQ 模块已经并入 messaging 聚合模块。
     *
     * @throws Exception POM 解析失败时抛出。
     */
    @Test
    void rabbitMqStarterShouldLiveUnderMessagingAggregator() throws Exception {
        Path repoRoot = locateRepoRoot();
        Path standaloneRabbitMq = repoRoot.resolve("vanta-starter-rabbitmq");
        Path messagingRabbitMq = repoRoot.resolve("vanta-starter-messaging/vanta-starter-messaging-rabbitmq");

        List<String> rootModules = readPomModules(repoRoot.resolve("pom.xml"));
        List<String> messagingModules = readPomModules(repoRoot.resolve("vanta-starter-messaging/pom.xml"));

        assertAll(
                "RabbitMQ starter 应统一归入 messaging 聚合模块",
                () -> assertFalse(Files.exists(standaloneRabbitMq), "不应保留根级 vanta-starter-rabbitmq 模块"),
                () -> assertTrue(Files.exists(messagingRabbitMq), "应存在 vanta-starter-messaging-rabbitmq 子模块"),
                () -> assertFalse(rootModules.contains("vanta-starter-rabbitmq"), "starter 根 POM 不应直接聚合 RabbitMQ"),
                () -> assertTrue(
                        messagingModules.contains("vanta-starter-messaging-rabbitmq"),
                        "messaging 聚合 POM 应聚合 RabbitMQ 适配器"));
    }
}
