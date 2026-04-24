package org.hhoa.mc.item_information.itemtooltip;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.neoforged.api.distmarker.Dist;
import org.junit.jupiter.api.Test;

class ItemTooltipTest {
    @Test
    void bootstrapsHandlersOnlyOnClient() {
        assertTrue(ItemTooltip.shouldBootstrapClient(Dist.CLIENT));
        assertFalse(ItemTooltip.shouldBootstrapClient(Dist.DEDICATED_SERVER));
    }
}
