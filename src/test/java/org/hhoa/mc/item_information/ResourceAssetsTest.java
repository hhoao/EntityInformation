package org.hhoa.mc.item_information;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ResourceAssetsTest {
    @Test
    void declaresNeoForgeItemModelDefinitions() {
        assertItemDefinition("data", "entity_information:item/data");
        assertItemDefinition("dictionary", "entity_information:item/dictionary");
    }

    @Test
    void itemModelsUseExplicitModernParentsAndTextureNamespaces() {
        assertItemModel("data", "minecraft:item/generated", "entity_information:item/data");
        assertItemModel("dictionary", "minecraft:item/generated", "minecraft:item/written_book");
    }

    private static void assertItemDefinition(String itemName, String modelPath) {
        JsonObject definition =
                readJson("/assets/entity_information/items/" + itemName + ".json");
        JsonObject model = definition.getAsJsonObject("model");

        assertEquals("minecraft:model", model.get("type").getAsString());
        assertEquals(modelPath, model.get("model").getAsString());
    }

    private static void assertItemModel(String itemName, String parent, String layer0) {
        JsonObject itemModel =
                readJson("/assets/entity_information/models/item/" + itemName + ".json");

        assertEquals(parent, itemModel.get("parent").getAsString());
        assertEquals(layer0, itemModel.getAsJsonObject("textures").get("layer0").getAsString());
    }

    private static JsonObject readJson(String path) {
        InputStream stream = ResourceAssetsTest.class.getResourceAsStream(path);
        assertNotNull(stream, "Missing resource " + path);
        return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .getAsJsonObject();
    }
}
