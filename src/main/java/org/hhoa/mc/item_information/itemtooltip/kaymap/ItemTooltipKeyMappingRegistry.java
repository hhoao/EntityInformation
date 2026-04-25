package org.hhoa.mc.item_information.itemtooltip.kaymap;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/**
 * KeyMappingRegistry
 *
 * @author xianxing
 * @since 2024/10/19
 */
public final class ItemTooltipKeyMappingRegistry {
    public static final KeyMapping SEARCH =
            new KeyMapping("key.gui.search", GLFW.GLFW_KEY_I, "key.open");
    public static final KeyMapping TOGGLE_TOOLTIP =
            new KeyMapping("key.itemtooltip", GLFW.GLFW_KEY_O, "key.open");
    public static final KeyMapping CHANGE_SEARCH_ENGINE =
            new KeyMapping("key.change_search_engine", GLFW.GLFW_KEY_K, "key.open");

    private ItemTooltipKeyMappingRegistry() {}

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(SEARCH);
        event.register(TOGGLE_TOOLTIP);
        event.register(CHANGE_SEARCH_ENGINE);
    }
}
