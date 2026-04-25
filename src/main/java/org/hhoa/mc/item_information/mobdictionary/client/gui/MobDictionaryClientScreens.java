package org.hhoa.mc.item_information.mobdictionary.client.gui;

import net.minecraft.client.Minecraft;

public final class MobDictionaryClientScreens {
    private MobDictionaryClientScreens() {}

    public static void openMobDictionary() {
        Minecraft.getInstance().setScreen(new MobDictionaryGui());
    }
}
