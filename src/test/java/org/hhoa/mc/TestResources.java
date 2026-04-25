package org.hhoa.mc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * TestResoruces
 *
 * @author xianxing
 * @since 2024/10/19
 */
public class TestResources {
    @Test
    public void test() throws IOException {
        URL resource = TestResources.class.getResource("/META-INF/neoforge.mods.toml");
        assertNotNull(resource, "generated neoforge.mods.toml should be available to tests");

        TomlParser tomlParser = new TomlParser();
        CommentedConfig parse =
                tomlParser.parse(new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8));
        Map<String, Object> config = parse.valueMap();
        List<CommentedConfig> mods = (List<CommentedConfig>) config.get("mods");
        CommentedConfig commentedConfig = mods.get(0);
        String modId = commentedConfig.get("modId");
        assertEquals("entity_information", modId);
    }
}
