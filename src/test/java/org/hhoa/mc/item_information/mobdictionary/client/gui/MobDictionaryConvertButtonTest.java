package org.hhoa.mc.item_information.mobdictionary.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class MobDictionaryConvertButtonTest {
    @Test
    void usesIconOnlyButtonMessageToAvoidScrollingTextInTinyControl() {
        assertEquals(Component.empty(), MobDictionaryConvertButton.VISIBLE_MESSAGE);
        assertFalse(MobDictionaryConvertButton.NARRATION_MESSAGE.getString().isBlank());
    }

    @Test
    void keepsButtonLargeEnoughForHoverTooltip() {
        assertEquals(10, MobDictionaryConvertButton.SIZE);
        assertEquals(7, MobDictionaryConvertButton.ICON_SIZE);
    }
}
