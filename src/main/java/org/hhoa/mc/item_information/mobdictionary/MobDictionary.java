package org.hhoa.mc.item_information.mobdictionary;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.hhoa.mc.item_information.ModInfo;
import org.hhoa.mc.item_information.mobdictionary.network.Dispatcher;
import org.hhoa.mc.item_information.registry.ModItems;

public final class MobDictionary {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModInfo.ID);

    public static final DeferredHolder<RecipeSerializer<?>, ShapedRecipe.Serializer>
            MY_CUSTOM_RECIPE_SERIALIZER =
                    RECIPE_SERIALIZERS.register("dictionary", ShapedRecipe.Serializer::new);

    public static final DeferredItem<Item> mobData = ModItems.MOB_DATA;
    public static final DeferredItem<Item> mobDictionary = ModItems.MOB_DICTIONARY;

    private static final EntityManager ENTITY_MANAGER = new EntityManager();
    private static final Dispatcher DISPATCHER = new Dispatcher();

    private MobDictionary() {}

    public static void bootstrap(IEventBus modBus) {
        modBus.register(new MobDictionaryFMLEventsHandler());
        NeoForge.EVENT_BUS.register(new MobDictionaryForgeEventsHandler());
        RECIPE_SERIALIZERS.register(modBus);
        ModItems.register(modBus);
    }

    public static Dispatcher getDispatcher() {
        return DISPATCHER;
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER;
    }
}
