package org.hhoa.mc.item_information.itemtooltip.kaymap;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

/**
 * KeyMappingRegistry
 *
 * @author xianxing
 * @since 2024/10/19
 */
public class ItemTooltipKeyMappingRegistry {
    public static KeyBinding searchKeyMapping;
    public static KeyBinding enableItemTooltipKeyMapping;
    public static KeyBinding changeSearchEngine;

    public static void registerSearchKeyMapping() {
        searchKeyMapping = new KeyBinding("key.gui.search", GLFW.GLFW_KEY_I, "key.open");
        enableItemTooltipKeyMapping =
                new KeyBinding("key.itemtooltip", GLFW.GLFW_KEY_O, "key.open");
        changeSearchEngine =
                new KeyBinding("key.change_search_engine", GLFW.GLFW_KEY_K, "key.open");
        ClientRegistry.registerKeyBinding(searchKeyMapping);
        ClientRegistry.registerKeyBinding(enableItemTooltipKeyMapping);
        ClientRegistry.registerKeyBinding(changeSearchEngine);
    }
}
