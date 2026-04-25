package org.hhoa.mc.item_information.itemtooltip;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * ItemFormation
 *
 * @author xianxing
 * @since 2024/11/5
 */
public class ItemTooltip {
    public static ItemTooltip INSTANCE;

    public ItemTooltip() {
        INSTANCE = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(ItemTooltipModEventsHandler.class);
        MinecraftForge.EVENT_BUS.register(ItemTooltipForgeEventsHandler.class);
    }
}
