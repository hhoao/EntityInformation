package org.hhoa.mc.item_information.mobdictionary.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import net.minecraft.client.gui.GuiGraphics;
import org.junit.jupiter.api.Test;

class MobDictionaryGuiBackgroundTest {
    @Test
    void overridesScreenBackgroundHooksToControlBackdropRendering() throws NoSuchMethodException {
        assertEquals(
                MobDictionaryGui.class,
                declaredMethod("renderBackground", GuiGraphics.class, int.class, int.class, float.class)
                        .getDeclaringClass());
        assertEquals(
                MobDictionaryGui.class,
                declaredMethod("renderBlurredBackground", float.class).getDeclaringClass());
        assertEquals(
                MobDictionaryGui.class,
                declaredMethod("renderTransparentBackground", GuiGraphics.class).getDeclaringClass());
    }

    private static Method declaredMethod(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        return MobDictionaryGui.class.getDeclaredMethod(name, parameterTypes);
    }
}
