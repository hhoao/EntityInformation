package org.hhoa.mc.item_information.mobdictionary;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.hhoa.mc.item_information.mobdictionary.capabilities.IFirstLoginCapability;
import org.hhoa.mc.item_information.mobdictionary.item.MobDataItem;
import org.hhoa.mc.item_information.mobdictionary.item.MobDictionaryItem;
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
        MobDictionary.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        PacketHandler.registerMessages();
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry()
                .registerAll(
                        new MobDictionaryItem(
                                        new Item.Properties()
                                                .stacksTo(1)
                                                .tab(CreativeModeTab.TAB_TOOLS))
                                .setRegistryName("dictionary"),
                        new MobDataItem(
                                        new Item.Properties()
                                                .stacksTo(1)
                                                .tab(CreativeModeTab.TAB_TOOLS))
                                .setRegistryName("data"));
    }

    @SubscribeEvent
    public void onGatherData(GatherDataEvent event) {
        MobDictionaryRecipeProvider myRecipeProvider =
                new MobDictionaryRecipeProvider(event.getGenerator());
        event.getGenerator().addProvider(myRecipeProvider);
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IFirstLoginCapability.class);
    }
}
