package org.hhoa.mc.item_information.itemtooltip.kaymap;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/**
 * KeyMappingRegistry
 *
 * @author xianxing
 * @since 2024/10/19
 */
public class ItemTooltipKeyMappingRegistry {
    public static KeyMapping searchKeyMapping;
    public static KeyMapping enableItemTooltipKeyMapping;
    public static KeyMapping changeSearchEngine;

    public static void registerSearchKeyMapping(RegisterKeyMappingsEvent event) {
        searchKeyMapping = new KeyMapping("key.gui.search", GLFW.GLFW_KEY_I, "key.open");
        enableItemTooltipKeyMapping =
                new KeyMapping("key.itemtooltip", GLFW.GLFW_KEY_O, "key.open");
        changeSearchEngine =
                new KeyMapping("key.change_search_engine", GLFW.GLFW_KEY_K, "key.open");

        event.register(searchKeyMapping);
        event.register(enableItemTooltipKeyMapping);
        event.register(changeSearchEngine);
    }
}
