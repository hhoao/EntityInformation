package org.hhoa.mc.item_information.mobdictionary.recipes;

import java.util.function.Consumer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import org.hhoa.mc.item_information.EntityInformation;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.jetbrains.annotations.NotNull;

public class MobDictionaryRecipeProvider extends RecipeProvider {
    public MobDictionaryRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ResourceLocation mobDictionaryRegistryName = EntityInformation.location("dictionary");

        ConditionalRecipe.builder()
                .addCondition(TrueCondition.INSTANCE)
                .addRecipe(
                        ShapedRecipeBuilder.shaped(
                                                RecipeCategory.TOOLS,
                                                MobDictionary.mobDictionary.get())
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
