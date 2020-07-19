package elec332.core.api.data.recipe;

import elec332.core.api.data.recipe.impl.ICookingRecipeBuilder;
import elec332.core.api.data.recipe.impl.IShapedRecipeBuilder;
import elec332.core.api.data.recipe.impl.IShapelessRecipeBuilder;
import elec332.core.api.data.recipe.impl.IStoneCutterRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 15-7-2020
 */
public interface IGroupedRecipeManager extends IRecipeBuilderBase<IGroupedRecipeManager> {

    void grouped(String group, Consumer<IGroupedRecipeManager> groupedRecipes);

    void grouped(Consumer<IGroupedRecipeManager> groupedRecipes);

    default IShapedRecipeBuilder shapedRecipe(IItemProvider item) {
        return shapedRecipe(item, 1);
    }

    IShapedRecipeBuilder shapedRecipe(IItemProvider item, int count);

    default IShapelessRecipeBuilder shapelessRecipe(IItemProvider item) {
        return shapelessRecipe(item, 1);
    }

    IShapelessRecipeBuilder shapelessRecipe(IItemProvider item, int count);

    default ICookingRecipeBuilder furnaceRecipe(IItemProvider item) {
        return furnaceRecipe(item, 1);
    }

    ICookingRecipeBuilder furnaceRecipe(IItemProvider item, int count);

    default ICookingRecipeBuilder blastFurnaceRecipe(IItemProvider item) {
        return blastFurnaceRecipe(item, 1);
    }

    ICookingRecipeBuilder blastFurnaceRecipe(IItemProvider item, int count);

    default ICookingRecipeBuilder smokingRecipe(IItemProvider item) {
        return smokingRecipe(item, 1);
    }

    ICookingRecipeBuilder smokingRecipe(IItemProvider item, int count);

    default ICookingRecipeBuilder campfireRecipe(IItemProvider item) {
        return campfireRecipe(item, 1);
    }

    ICookingRecipeBuilder campfireRecipe(IItemProvider item, int count);


    default IStoneCutterRecipeBuilder stonecuttingRecipe(IItemProvider item) {
        return stonecuttingRecipe(item, 1);
    }

    IStoneCutterRecipeBuilder stonecuttingRecipe(IItemProvider item, int count);

    default <T extends IRecipeBuilder<T>> T customRecipe(IRecipeType<T> type, ItemStack item) {
        return customRecipe(type, item.getItem(), item.getCount()).withTag(item.getTag());
    }

    default <T extends IRecipeBuilder<T>> T customRecipe(IRecipeType<T> type, IItemProvider item) {
        return customRecipe(type, item, 1);
    }

    <T extends IRecipeBuilder<T>> T customRecipe(IRecipeType<T> type, IItemProvider item, int count);

}
