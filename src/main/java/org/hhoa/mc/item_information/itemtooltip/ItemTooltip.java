package org.hhoa.mc.item_information.itemtooltip;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

public final class ItemTooltip {
    private ItemTooltip() {}

    public static void bootstrap(IEventBus modBus) {
        if (!shouldBootstrapClient(FMLEnvironment.dist)) {
            return;
        }
        modBus.register(new ItemTooltipModEventsHandler());
        NeoForge.EVENT_BUS.register(new ItemTooltipForgeEventsHandler());
    }

    static boolean shouldBootstrapClient(Dist dist) {
        return dist == Dist.CLIENT;
    }
}
