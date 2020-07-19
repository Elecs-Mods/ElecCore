package elec332.core.api.data.recipe;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 15-7-2020
 */
public interface IRecipeType<T extends IRecipeBuilder<T>> {

    T createBuilder(Consumer<IFinishedRecipe> registry, IItemProvider result, int resultCount, String group, Map<Character, Ingredient> keys, Map<String, ICriterionInstance> criteria);

}
