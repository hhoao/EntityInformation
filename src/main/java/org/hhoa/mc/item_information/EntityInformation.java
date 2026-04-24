package org.hhoa.mc.item_information;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.hhoa.mc.item_information.config.Configs;
import org.hhoa.mc.item_information.itemtooltip.ItemTooltip;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;

@Mod(ModInfo.ID)
public class EntityInformation {
    public EntityInformation(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(EntityInformation::onConfigLoading);
        modBus.addListener(EntityInformation::onConfigReloading);
        modContainer.registerConfig(ModConfig.Type.COMMON, Configs.SPEC);
        Configs.syncFromConfig();
        ItemTooltip.bootstrap(modBus);
        MobDictionary.bootstrap(modBus);
    }

    private static void onConfigLoading(ModConfigEvent.Loading event) {
        syncConfig(event);
    }

    private static void onConfigReloading(ModConfigEvent.Reloading event) {
        syncConfig(event);
    }

    private static void syncConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() == Configs.SPEC) {
            Configs.syncFromConfig();
        }
    }

    public static ResourceLocation location(String path) {
        return ModInfo.location(path);
    }

    public static String getModRelevantText(String string) {
        return ModInfo.ID + "." + string;
    }
}
