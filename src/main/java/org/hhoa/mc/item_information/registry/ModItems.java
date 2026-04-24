package org.hhoa.mc.item_information.registry;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.hhoa.mc.item_information.ModInfo;
import org.hhoa.mc.item_information.mobdictionary.item.MobDataItem;
import org.hhoa.mc.item_information.mobdictionary.item.MobDictionaryItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModInfo.ID);

    public static final DeferredItem<Item> MOB_DATA =
            ITEMS.register("data", () -> new MobDataItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> MOB_DICTIONARY =
            ITEMS.register(
                    "dictionary", () -> new MobDictionaryItem(new Item.Properties().stacksTo(1)));

    private ModItems() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
