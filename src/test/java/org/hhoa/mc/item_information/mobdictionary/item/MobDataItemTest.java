package org.hhoa.mc.item_information.mobdictionary.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

class MobDataItemTest {
    @Test
    void storesEntityNameInsideCustomData() {
        ItemStack stack = new ItemStack(Items.PAPER);

        MobDataItem.setEntityName(stack, "entity.minecraft.cow");

        assertEquals("entity.minecraft.cow", MobDataItem.getEntityName(stack));
        assertTrue(MobDataItem.hasEntityName(stack));
    }
}
