package org.hhoa.mc.item_information.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TextRenderer {
    public static void drawSimpleText(
            Font font, GuiGraphics poseStack, String text, float x, float y, int color) {
        Component component = Component.literal(text);
        drawSimpleText(font, poseStack, component, x, y, color);
    }

    public static void drawSimpleText(
            Font font, GuiGraphics poseStack, Component component, float x, float y, int color) {
        poseStack.drawString(
                font, component, Math.round(x), Math.round(y), visibleTextColor(color), false);
    }

    static int visibleTextColor(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }
}
