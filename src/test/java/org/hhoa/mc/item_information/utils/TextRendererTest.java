package org.hhoa.mc.item_information.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TextRendererTest {
    @Test
    void makesLegacyRgbTextColorsOpaqueForModernGuiRendering() {
        assertEquals(0xFF303030, TextRenderer.visibleTextColor(0x303030));
        assertEquals(0xFF404040, TextRenderer.visibleTextColor(0x404040));
        assertEquals(0xFFFFFFFF, TextRenderer.visibleTextColor(0xFFFFFF));
    }

    @Test
    void keepsExplicitAlphaTextColorsUnchanged() {
        assertEquals(0x80303030, TextRenderer.visibleTextColor(0x80303030));
        assertEquals(0xFFFFFFFF, TextRenderer.visibleTextColor(0xFFFFFFFF));
    }
}
