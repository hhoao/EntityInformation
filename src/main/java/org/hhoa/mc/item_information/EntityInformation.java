package org.hhoa.mc.item_information;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.hhoa.mc.item_information.itemtooltip.ItemTooltip;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;

@Mod(ModInfo.ID)
public class EntityInformation {
    private static MobDictionary mobDictionary;
    private static ItemTooltip itemTooltip;

    public EntityInformation() {
        mobDictionary = new MobDictionary();
        itemTooltip = new ItemTooltip();
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(ModInfo.ID, path);
    }

    public static String getModRelevantText(String string) {
        return ModInfo.ID + "." + string;
    }

    public static MobDictionary getMobDictionary() {
        return mobDictionary;
    }

    public static ItemTooltip getItemTooltip() {
        return itemTooltip;
    }
}
