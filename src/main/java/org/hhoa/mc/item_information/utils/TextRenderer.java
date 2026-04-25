package org.hhoa.mc.item_information.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

public class TextRenderer {
    public static void drawSimpleText(
            Font font, GuiGraphics poseStack, String text, float x, float y, int color) {
        Component component = Component.literal(text);
        drawSimpleText(font, poseStack, component, x, y, color);
    }

    public static void drawSimpleText(
            Font font, GuiGraphics poseStack, Component component, float x, float y, int color) {
        MultiBufferSource.BufferSource bufferSource =
                Minecraft.getInstance().renderBuffers().bufferSource();

        font.drawInBatch(
                component, // 文本
                x, // X 坐标
                y, // Y 坐标
                color, // 颜色
                false, // 阴影
                new Matrix4f(), // PoseStack
                poseStack.bufferSource(), // 缓冲区
                Font.DisplayMode.NORMAL, // 渲染模式
                0, // 背景颜色
                15728880 // 光照参数
                );
    }
}
