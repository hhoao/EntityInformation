package org.hhoa.mc.item_information.mobdictionary.recipes;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.jetbrains.annotations.NotNull;

public class MobDictionaryRecipeProvider extends RecipeProvider {
    public MobDictionaryRecipeProvider(DataGenerator p_125973_) {
        super(p_125973_);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ResourceLocation mobDictionaryRegistryName =
                Objects.requireNonNull(MobDictionary.mobDictionary.getRegistryName());

        ConditionalRecipe.builder()
                .addCondition(TrueCondition.INSTANCE)
                .addRecipe(
                        ShapedRecipeBuilder.shaped(MobDictionary.mobDictionary)
                                        .pattern("bs ")
                                        .pattern("   ")
                                        .pattern("   ")
                                        .define('b', Items.BOOK)
                                        .define('s', Items.SPYGLASS)
                                        .unlockedBy("has_book", has(Items.BOOK))
                                ::save)
                .generateAdvancement()
                .build(consumer, mobDictionaryRegistryName);
    }
}
