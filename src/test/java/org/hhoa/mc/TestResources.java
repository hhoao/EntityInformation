package org.hhoa.mc;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        URL resource = TestResources.class.getResource("/META-INF/mods.toml");
        TomlParser tomlParser = new TomlParser();
        CommentedConfig parse =
                tomlParser.parse(Files.newBufferedReader(new File(resource.getPath()).toPath()));
        Set<? extends CommentedConfig.Entry> entries = parse.entrySet();
        Map<String, Object> config = parse.valueMap();
        List<CommentedConfig> mods = (List<CommentedConfig>) config.get("mods");
        CommentedConfig commentedConfig = mods.get(0);
        String modId = commentedConfig.get("modId");
        System.out.println(modId);
    }
}
