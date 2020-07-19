package elec332.core.api.data.recipe.impl;

import elec332.core.api.data.recipe.IRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;

/**
 * Created by Elec332 on 15-7-2020
 */
public interface ICookingRecipeBuilder extends IRecipeBuilder<ICookingRecipeBuilder> {

    ICookingRecipeBuilder withOutput(Character input, float experience, int cookingTime);

    ICookingRecipeBuilder withOutput(Tag<Item> input, float experience, int cookingTime);

    ICookingRecipeBuilder withOutput(IItemProvider input, float experience, int cookingTime);

    ICookingRecipeBuilder withOutput(Ingredient input, float experience, int cookingTime);

}
