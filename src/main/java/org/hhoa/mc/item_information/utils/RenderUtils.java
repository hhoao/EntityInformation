package org.hhoa.mc.item_information.utils;

import net.minecraft.client.gui.FontRenderer;

/**
 * RenderUtils
 *
 * @author xianxing
 * @since 2024/11/17
 */
public class RenderUtils {
    public int getFontWidth(FontRenderer fontRenderer, String text) {
        return fontRenderer.getStringWidth(text);
    }
}
