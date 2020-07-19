package elec332.core.data.recipe;

import com.google.gson.JsonObject;
import elec332.core.api.data.recipe.impl.ICookingRecipeBuilder;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 15-7-2020
 */
public class CookingRecipeBuilder extends AbstractRecipeBuilder<ICookingRecipeBuilder> implements ICookingRecipeBuilder {

    public CookingRecipeBuilder(Consumer<IFinishedRecipe> registry, IItemProvider result, int resultCount, String group, Map<Character, Ingredient> keys, Map<String, ICriterionInstance> criteria, CookingRecipeSerializer<?> serializer) {
        super(registry, result, resultCount, group, keys, criteria);
        this.serializer = serializer;
        this.builder = null;
    }

    private final CookingRecipeSerializer<?> serializer;
    private net.minecraft.data.CookingRecipeBuilder builder;

    @Override
    protected void register(IFinishedRecipe recipe) {
        if (resultCount > 1 || tag != null) {
            final int finalCount = resultCount;
            final CompoundNBT finalTag = tag == null ? null : tag.copy();
            recipe = new WrappedFinishedRecipe(recipe) {

                @Override
                public void serialize(@Nonnull JsonObject json) {
                    super.serialize(json);
                    JsonObject object = new JsonObject();
                    object.addProperty("item", json.get("result").getAsString());
                    if (finalCount > 1) {
                        object.addProperty("count", finalCount);
                    }
                    if (finalTag != null) {
                        object.addProperty("nbt", tag.toString());
                    }
                    json.remove("result");
                    json.add("result", object);
                }

            };
        }
        super.register(recipe);
    }

    @Override
    public ICookingRecipeBuilder withOutput(Character input, float experience, int cookingTime) {
        return withOutput(getIngredient(input), experience, cookingTime);
    }

    @Override
    public ICookingRecipeBuilder withOutput(Tag<Item> input, float experience, int cookingTime) {
        return withOutput(Ingredient.fromTag(input), experience, cookingTime);
    }

    @Override
    public ICookingRecipeBuilder withOutput(IItemProvider input, float experience, int cookingTime) {
        return withOutput(Ingredient.fromItems(input), experience, cookingTime);
    }

    @Override
    public ICookingRecipeBuilder withOutput(Ingredient input, float experience, int cookingTime) {
        if (builder != null) {
            throw new IllegalStateException("Output already defined");
        }
        builder = net.minecraft.data.CookingRecipeBuilder.cookingRecipe(input, getResult(), experience, cookingTime, serializer);
        return this;
    }

    @Override
    public void build(ResourceLocation id) {
        if (builder == null) {
            throw new IllegalStateException("No output defined");
        }
        builder.build(this::register, id);
    }

}
