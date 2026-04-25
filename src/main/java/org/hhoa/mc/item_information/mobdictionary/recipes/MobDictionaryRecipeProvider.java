package org.hhoa.mc.item_information.mobdictionary.recipes;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.TrueCondition;
import net.neoforged.neoforge.common.crafting.ConditionalRecipeOutput;
import org.hhoa.mc.item_information.EntityInformation;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;
import org.jetbrains.annotations.NotNull;

public class MobDictionaryRecipeProvider extends RecipeProvider {
    public MobDictionaryRecipeProvider(
            PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        ResourceLocation mobDictionaryRegistryName = EntityInformation.location("dictionary");
        RecipeOutput conditionalOutput =
                new ConditionalRecipeOutput(recipeOutput, new TrueCondition[] {TrueCondition.INSTANCE});

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, MobDictionary.mobDictionary.get())
                .pattern("bs ")
                .pattern("   ")
                .pattern("   ")
                .define('b', Items.BOOK)
                .define('s', Items.SPYGLASS)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(conditionalOutput, mobDictionaryRegistryName);
    }
}
