package org.hhoa.mc.item_information.mobdictionary.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.hhoa.mc.item_information.EntityInformation;
import org.hhoa.mc.item_information.mobdictionary.messages.Texts;

final class MobDictionaryConvertButton extends Button {
    static final int SIZE = 10;
    static final int ICON_SIZE = 7;
    static final Component VISIBLE_MESSAGE = Component.empty();
    static final Component NARRATION_MESSAGE = Texts.OUTPUT_PIECE.getTextComponent();
    private static final ResourceLocation ICON =
            EntityInformation.location("textures/item/data.png");

    MobDictionaryConvertButton(int x, int y, OnPress onPress, Tooltip tooltip) {
        super(
                x,
                y,
                SIZE,
                SIZE,
                VISIBLE_MESSAGE,
                onPress,
                messageSupplier -> Component.empty().append(NARRATION_MESSAGE));
        setTooltip(tooltip);
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, int color) {
        // Icon-only control: keep the label for narration/tooltip, but never render scrolling text.
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        int iconX = getX() + (getWidth() - ICON_SIZE) / 2;
        int iconY = getY() + (getHeight() - ICON_SIZE) / 2;
        guiGraphics.blit(ICON, iconX, iconY, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, 16, 16);
    }
}
