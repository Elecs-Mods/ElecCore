package elec332.core.api.data.recipe.impl;

import elec332.core.api.data.recipe.IRecipeBuilder;

/**
 * Created by Elec332 on 15-7-2020
 */
public interface IShapedRecipeBuilder extends IRecipeBuilder<IShapedRecipeBuilder> {

    IShapedRecipeBuilder patternLine(String pattern);

}
