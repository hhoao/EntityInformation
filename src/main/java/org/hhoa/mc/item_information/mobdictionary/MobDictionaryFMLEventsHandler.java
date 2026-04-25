package org.hhoa.mc.item_information.mobdictionary;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.hhoa.mc.item_information.mobdictionary.capabilities.IFirstLoginCapability;
import org.hhoa.mc.item_information.mobdictionary.network.PacketHandler;
import org.hhoa.mc.item_information.mobdictionary.recipes.MobDictionaryRecipeProvider;

/**
 * MobDictionaryFMLEventsHandler
 *
 * @author xianxing
 * @since 2024/11/4
 */
public class MobDictionaryFMLEventsHandler {
    @SubscribeEvent
    public void preInit(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
    }

    @SubscribeEvent
    public void onGatherData(GatherDataEvent event) {
        MobDictionaryRecipeProvider myRecipeProvider =
                new MobDictionaryRecipeProvider(event.getGenerator().getPackOutput());
        event.getGenerator().addProvider(true, myRecipeProvider);
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IFirstLoginCapability.class);
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
                item.setTag(nbt);
                event.accept(item);
            }
        }
    }
}
