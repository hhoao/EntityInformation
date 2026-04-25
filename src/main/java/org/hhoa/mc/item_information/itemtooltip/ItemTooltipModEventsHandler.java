package org.hhoa.mc.item_information.itemtooltip;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.Logger;
import org.hhoa.mc.item_information.itemtooltip.kaymap.ItemTooltipKeyMappingRegistry;
import org.hhoa.mc.item_information.utils.LoggerUtils;

public class ItemTooltipModEventsHandler {
    public static final Logger LOGGER = LoggerUtils.getLogger(ItemTooltipModEventsHandler.class);

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemTooltipKeyMappingRegistry.registerSearchKeyMapping();
    }
}
