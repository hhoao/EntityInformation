package org.hhoa.mc.item_information.mobdictionary.recipes;

import java.util.function.Consumer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Items;
import org.hhoa.mc.item_information.mobdictionary.MobDictionary;

public class MobDictionaryRecipeProvider extends RecipeProvider {
    public MobDictionaryRecipeProvider(DataGenerator p_125973_) {
        super(p_125973_);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapeBasedRecipeBuilder.shapedRecipe(MobDictionary.mobDictionary)
                .patternLine("bs ")
                .patternLine("   ")
                .patternLine("   ")
                .key('b', Items.BOOK)
                .key('s', Items.COMPASS)
                .addCriterion("has_book", hasItem(Items.BOOK))
                .addCriterion("has_compass", hasItem(Items.COMPASS))
                .build(consumer);
    }
}
