package elec332.core.api.data.recipe;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;

/**
 * Created by Elec332 on 15-7-2020
 */
interface IRecipeBuilderBase<T extends IRecipeBuilderBase<T>> {

    T addCriterion(String name, ICriterionInstance criterion);

    default T key(Character symbol, Tag<Item> tag) {
        return this.key(symbol, Ingredient.fromTag(tag));
    }

    default T key(Character symbol, IItemProvider item) {
        return this.key(symbol, Ingredient.fromItems(item));
    }

    T key(Character symbol, Ingredient ingredient);

}
