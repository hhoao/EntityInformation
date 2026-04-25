package org.hhoa.mc.item_information;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class ModInfoTest {
    @Test
    void exposesStableModIdentity() throws IOException {
        Properties properties = loadGradleProperties();

        assertEquals("entity_information", properties.getProperty("mod_id"));
        assertEquals("EntityInformation", properties.getProperty("mod_name"));
    }

    @Test
    void exposesAlignedNeoforge1218BuildProperties() throws IOException {
        Properties properties = loadGradleProperties();

        assertEquals("1.21.8", properties.getProperty("minecraft_version"));
        assertEquals("[1.21.8,1.22)", properties.getProperty("minecraft_version_range"));
        assertEquals("21.8.53", properties.getProperty("neo_version"));
        assertEquals("[21,)", properties.getProperty("neo_version_range"));
        assertEquals("[4,)", properties.getProperty("loader_version_range"));
        assertEquals("1.21.8", properties.getProperty("parchment_minecraft_version"));
        assertEquals("2025.09.14", properties.getProperty("parchment_mappings_version"));
    }

    private static Properties loadGradleProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(findProjectRoot().resolve("gradle.properties"))) {
            properties.load(inputStream);
        }
        return properties;
    }

    private static Path findProjectRoot() throws IOException {
        Path current = Path.of("").toRealPath();
        while (current != null) {
            if (Files.exists(current.resolve("gradle.properties"))
                    && Files.exists(current.resolve("settings.gradle"))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IOException("Could not locate project root from test working directory");
    }
}
