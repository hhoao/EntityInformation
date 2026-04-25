package org.hhoa.mc.item_information.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
            ITEMS.register(
                    "data",
                    key ->
                            new MobDataItem(
                                    new Item.Properties()
                                            .setId(ResourceKey.create(Registries.ITEM, key))
                                            .stacksTo(1)));
    public static final DeferredItem<Item> MOB_DICTIONARY =
            ITEMS.register(
                    "dictionary",
                    key ->
                            new MobDictionaryItem(
                                    new Item.Properties()
                                            .setId(ResourceKey.create(Registries.ITEM, key))
                                            .stacksTo(1)));

    private ModItems() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
