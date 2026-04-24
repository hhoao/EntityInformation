package org.hhoa.mc.item_information.itemtooltip;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ItemTooltipServiceTest {
    @Test
    void parsesExistingItemInfoJson() throws Exception {
        ItemTooltipService service = new ItemTooltipService();
        try (var stream = ItemTooltipServiceTest.class.getResourceAsStream(
                "/assets/entity_information/item_infos/minecraft/granite.json")) {
            var info = service.readItemInfo(stream);

            assertTrue(info.getInfos().containsKey("简介"));
            assertFalse(info.getInfos().get("简介").isEmpty());
            assertTrue(info.getInfos().containsKey("用途"));
        }
    }
}
