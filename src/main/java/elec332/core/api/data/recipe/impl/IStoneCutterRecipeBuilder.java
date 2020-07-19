package elec332.core.api.data.recipe.impl;

import elec332.core.api.data.recipe.IRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;

/**
 * Created by Elec332 on 15-7-2020
 */
public interface IStoneCutterRecipeBuilder extends IRecipeBuilder<IStoneCutterRecipeBuilder> {

    IStoneCutterRecipeBuilder withOutput(Character input);

    IStoneCutterRecipeBuilder withOutput(Tag<Item> input);

    IStoneCutterRecipeBuilder withOutput(IItemProvider input);

    IStoneCutterRecipeBuilder withOutput(Ingredient input);

}
