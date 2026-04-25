package org.hhoa.mc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class TemplateResourceTest {
    private static final Pattern MOD_ID_PATTERN =
            Pattern.compile("(?m)^modId\\s*=\\s*\"entity_information\"$");
    private static final Pattern DISPLAY_NAME_PATTERN =
            Pattern.compile("(?m)^displayName\\s*=\\s*\"EntityInformation\"$");
    private static final Pattern NEOFORGE_DEPENDENCY_PATTERN =
            Pattern.compile(
                    "(?s)\\[\\[dependencies\\.\"entity_information\"\\]\\].*?"
                            + "modId\\s*=\\s*\"neoforge\".*?"
                            + "versionRange\\s*=\\s*\"\\[21,\\)\"");
    private static final Pattern MINECRAFT_DEPENDENCY_PATTERN =
            Pattern.compile(
                    "(?s)\\[\\[dependencies\\.\"entity_information\"\\]\\].*?"
                            + "modId\\s*=\\s*\"minecraft\".*?"
                            + "versionRange\\s*=\\s*\"\\[1\\.21\\.8,1\\.22\\)\"");

    @Test
    void generatesNeoforgeMetadataFromTemplate() throws IOException {
        URL resource = TemplateResourceTest.class.getResource("/META-INF/neoforge.mods.toml");
        assertNotNull(resource, "generated neoforge.mods.toml should be on the test classpath");

        String text = new String(resource.openStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(MOD_ID_PATTERN.matcher(text).find());
        assertTrue(DISPLAY_NAME_PATTERN.matcher(text).find());
        assertTrue(text.contains("loaderVersion = \"[4,)\""));
        assertTrue(NEOFORGE_DEPENDENCY_PATTERN.matcher(text).find());
        assertTrue(MINECRAFT_DEPENDENCY_PATTERN.matcher(text).find());
        assertEquals(-1, text.indexOf("examplemod"), "template placeholders must be removed");
    }
}
