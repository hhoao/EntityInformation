package org.hhoa.mc.item_information.mobdictionary;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.hhoa.mc.item_information.mobdictionary.capabilities.FirstLoginCapabilityImpl;
import org.hhoa.mc.item_information.mobdictionary.capabilities.FirstLoginCapabilityStorage;
import org.hhoa.mc.item_information.mobdictionary.capabilities.IFirstLoginCapability;
import org.hhoa.mc.item_information.mobdictionary.capabilities.MobDataCapability;
import org.hhoa.mc.item_information.mobdictionary.capabilities.MobDataCapabilityImpl;
import org.hhoa.mc.item_information.mobdictionary.capabilities.MobDataCapabilityStorage;
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
        CapabilityManager.INSTANCE.register(
                IFirstLoginCapability.class,
                new FirstLoginCapabilityStorage(),
                FirstLoginCapabilityImpl::new);
        CapabilityManager.INSTANCE.register(
                MobDataCapability.class,
                new MobDataCapabilityStorage(),
                MobDataCapabilityImpl::new);
        PacketHandler.registerMessages();
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry()
                .registerAll(
                        new MobDictionaryItem(
                                        new Item.Properties()
                                                .maxStackSize(1)
                                                .group(ItemGroup.TOOLS))
                                .setRegistryName("dictionary"),
                        new MobDataItem(
                                        new Item.Properties()
                                                .maxStackSize(1)
                                                .group(ItemGroup.TOOLS))
                                .setRegistryName("data"));
    }

    @SubscribeEvent
    public void onGatherData(GatherDataEvent event) {
        MobDictionaryRecipeProvider myRecipeProvider =
                new MobDictionaryRecipeProvider(event.getGenerator());
        event.getGenerator().addProvider(myRecipeProvider);
    }
}
