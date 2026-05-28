package com.vanta.starter.web.autoconfigure;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StarterDefaultResourceTest {

    @Test
    void shouldPackageDefaultResponseAndServerResources() throws IOException {
        assertAll(
                () -> assertResourceContains("default-response.yml", "graceful-response"),
                () -> assertResourceContains("default-server.yml", "server:")
        );
    }

    private void assertResourceContains(String resourceName, String expectedText) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assertNotNull(classLoader.getResource(resourceName), resourceName + " should exist on the classpath");

        try (InputStream inputStream = classLoader.getResourceAsStream(resourceName)) {
            assertNotNull(inputStream, resourceName + " should be readable");
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(!content.isBlank(), resourceName + " should not be blank");
            assertTrue(content.contains(expectedText), resourceName + " should contain " + expectedText);
        }
    }
}
