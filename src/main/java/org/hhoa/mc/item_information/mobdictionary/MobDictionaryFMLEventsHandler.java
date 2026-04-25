package org.hhoa.mc.item_information.mobdictionary;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.hhoa.mc.item_information.mobdictionary.network.MobDictionaryPayloads;
import org.hhoa.mc.item_information.mobdictionary.recipes.MobDictionaryRecipeProvider;

/**
 * MobDictionaryFMLEventsHandler
 *
 * @author xianxing
 * @since 2024/11/4
 */
public class MobDictionaryFMLEventsHandler {
    @SubscribeEvent
    public void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        MobDictionaryPayloads.register(event);
    }

    @SubscribeEvent
    public void onGatherData(GatherDataEvent event) {
        MobDictionaryRecipeProvider myRecipeProvider =
                new MobDictionaryRecipeProvider(
                        event.getGenerator().getPackOutput(), event.getLookupProvider());
        event.getGenerator().addProvider(true, myRecipeProvider);
    }

    @SubscribeEvent
    public void onRegisterCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(MobDictionary.mobDictionary);
            event.accept(MobDictionary.mobData);
            for (String name : MobDictionary.getEntityManager().getEntityNames()) {
                ItemStack item = new ItemStack(MobDictionary.mobData.get());
                CompoundTag nbt = new CompoundTag();
                nbt.putString("Name", name);
                item.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                event.accept(item);
            }
        }
    }
}
