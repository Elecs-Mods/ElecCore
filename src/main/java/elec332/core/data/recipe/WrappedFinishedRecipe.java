package elec332.core.data.recipe;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 15-7-2020
 */
public class WrappedFinishedRecipe implements IFinishedRecipe {

    public WrappedFinishedRecipe(IFinishedRecipe recipe) {
        this.recipe = Preconditions.checkNotNull(recipe);
    }

    private final IFinishedRecipe recipe;

    @Override
    public void serialize(@Nonnull JsonObject json) {
        recipe.serialize(json);
    }

    @Nonnull
    @Override
    public ResourceLocation getID() {
        return recipe.getID();
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return recipe.getSerializer();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
        return recipe.getAdvancementJson();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
        return recipe.getAdvancementID();
    }

}
