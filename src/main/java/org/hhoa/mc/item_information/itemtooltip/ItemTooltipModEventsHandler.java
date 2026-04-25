package org.hhoa.mc.item_information.itemtooltip;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.hhoa.mc.item_information.itemtooltip.kaymap.ItemTooltipKeyMappingRegistry;

public class ItemTooltipModEventsHandler {
    @SubscribeEvent
    public void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ItemTooltipKeyMappingRegistry.register(event);
    }
}
