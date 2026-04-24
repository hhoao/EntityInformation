package org.hhoa.mc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class TemplateResourceTest {
    @Test
    void generatesNeoforgeMetadataFromTemplate() throws IOException {
        URL resource = TemplateResourceTest.class.getResource("/META-INF/neoforge.mods.toml");
        assertNotNull(resource, "generated neoforge.mods.toml should be on the test classpath");

        String text = new String(resource.openStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(text.contains("modId=\"entity_information\""));
        assertTrue(text.contains("displayName=\"EntityInformation\""));
        assertTrue(text.contains("modId=\"neoforge\""));
        assertEquals(-1, text.indexOf("examplemod"), "template placeholders must be removed");
    }
}
