package elec332.core.data.recipe;

import elec332.core.api.data.recipe.IRecipeType;
import elec332.core.api.data.recipe.impl.ICookingRecipeBuilder;
import elec332.core.api.data.recipe.impl.IShapedRecipeBuilder;
import elec332.core.api.data.recipe.impl.IShapelessRecipeBuilder;
import elec332.core.api.data.recipe.impl.IStoneCutterRecipeBuilder;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;

import java.util.function.Function;

/**
 * Created by Elec332 on 15-7-2020
 */
public class RecipeTypes {

    public static final Function<CookingRecipeSerializer<?>, IRecipeType<ICookingRecipeBuilder>> COOKING_RECIPE_TYPE_GETTER = serializer ->
            (IRecipeType<ICookingRecipeBuilder>) (registry, result, resultCount, group, keys, criteria) -> new CookingRecipeBuilder(registry, result, resultCount, group, keys, criteria, serializer);

    public static final IRecipeType<IShapedRecipeBuilder> SHAPED_RECIPE = ShapedRecipeBuilder::new;
    public static final IRecipeType<IShapelessRecipeBuilder> SHAPELESS_RECIPE = ShapelessRecipeBuilder::new;
    public static final IRecipeType<ICookingRecipeBuilder> FURNACE_RECIPE = COOKING_RECIPE_TYPE_GETTER.apply(IRecipeSerializer.SMELTING);
    public static final IRecipeType<ICookingRecipeBuilder> BLASTFURNACE_RECIPE = COOKING_RECIPE_TYPE_GETTER.apply(IRecipeSerializer.BLASTING);
    public static final IRecipeType<ICookingRecipeBuilder> SMOKING_RECIPE = COOKING_RECIPE_TYPE_GETTER.apply(IRecipeSerializer.SMOKING);
    public static final IRecipeType<ICookingRecipeBuilder> CAMPFIRE_RECIPE = COOKING_RECIPE_TYPE_GETTER.apply(IRecipeSerializer.CAMPFIRE_COOKING);
    public static final IRecipeType<IStoneCutterRecipeBuilder> STONECUTTING_RECIPE = StoneCutterRecipeBuilder::new;

}
