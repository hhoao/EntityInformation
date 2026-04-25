package org.hhoa.mc.item_information.mobdictionary.recipes;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;

public class MobDictionaryRecipeProvider extends RecipeProvider {
    public MobDictionaryRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        this.shaped(RecipeCategory.TOOLS, MobDictionary.mobDictionary.get())
                .pattern("bs ")
                .pattern("   ")
                .pattern("   ")
                .define('b', Items.BOOK)
                .define('s', Items.SPYGLASS)
                .unlockedBy("has_book", this.has(Items.BOOK))
                .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(
                HolderLookup.Provider registries, RecipeOutput output) {
            return new MobDictionaryRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "EntityInformation recipes";
        }
    }
}
