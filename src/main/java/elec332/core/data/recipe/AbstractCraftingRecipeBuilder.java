package elec332.core.data.recipe;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import elec332.core.api.data.recipe.IRecipeBuilder;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 15-7-2020
 */
public abstract class AbstractCraftingRecipeBuilder<T extends IRecipeBuilder<T>> extends AbstractRecipeBuilder<T> {

    public AbstractCraftingRecipeBuilder(Consumer<IFinishedRecipe> registry, IItemProvider result, int resultCount, String group, Map<Character, Ingredient> keys, Map<String, ICriterionInstance> criteria) {
        super(registry, result, resultCount, group, keys, criteria);
    }

    @Override
    protected void register(IFinishedRecipe recipe) {
        if (tag != null) {
            final CompoundNBT finalTag = tag.copy();
            recipe = new WrappedFinishedRecipe(recipe) {

                @Override
                public void serialize(@Nonnull JsonObject json) {
                    super.serialize(json);
                    Preconditions.checkNotNull(json.getAsJsonObject("result")).addProperty("nbt", finalTag.toString());
                }

            };
        }
        super.register(recipe);
    }

}
