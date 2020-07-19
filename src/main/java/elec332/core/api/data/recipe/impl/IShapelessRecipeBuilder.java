package elec332.core.api.data.recipe.impl;

import elec332.core.api.data.recipe.IRecipeBuilder;
import elec332.core.data.recipe.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;

/**
 * Created by Elec332 on 15-7-2020
 */
public interface IShapelessRecipeBuilder extends IRecipeBuilder<IShapelessRecipeBuilder> {

    ShapelessRecipeBuilder addIngredient(Tag<Item> tag);

    ShapelessRecipeBuilder addIngredient(IItemProvider item);

    ShapelessRecipeBuilder addIngredient(IItemProvider item, int quantity);

    ShapelessRecipeBuilder addIngredient(Ingredient ingredient);

    ShapelessRecipeBuilder addIngredient(Ingredient ingredient, int quantity);

    ShapelessRecipeBuilder addIngredient(Character ingredient);

    ShapelessRecipeBuilder addIngredient(Character ingredient, int quantity);

}
