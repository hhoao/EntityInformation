package org.hhoa.mc.item_information.mobdictionary;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;
import org.hhoa.mc.item_information.ModInfo;
import org.hhoa.mc.item_information.mobdictionary.network.Dispatcher;

public class MobDictionary {
    public static MobDictionary INSTANCE;

    @ObjectHolder(("entity_information:dictionary"))
    public static Item mobDictionary;

    @ObjectHolder("entity_information:data")
    public static Item mobData;

    private static EntityManager entityManager;

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModInfo.ID);

    public static final RegistryObject<ShapedRecipe.Serializer> MY_CUSTOM_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register("dictionary", ShapedRecipe.Serializer::new);
    private static Dispatcher dispatcher;

    public MobDictionary() {
        INSTANCE = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(new MobDictionaryFMLEventsHandler());
        MinecraftForge.EVENT_BUS.register(new MobDictionaryForgeEventsHandler());
        entityManager = new EntityManager();
        dispatcher = new Dispatcher();
    }

    public static Dispatcher getDispatcher() {
        return dispatcher;
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }
}
