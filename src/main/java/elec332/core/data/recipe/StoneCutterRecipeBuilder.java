package elec332.core.data.recipe;

import elec332.core.api.data.recipe.impl.IStoneCutterRecipeBuilder;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 15-7-2020
 */
public class StoneCutterRecipeBuilder extends AbstractRecipeBuilder<IStoneCutterRecipeBuilder> implements IStoneCutterRecipeBuilder {

    public StoneCutterRecipeBuilder(Consumer<IFinishedRecipe> registry, IItemProvider result, int resultCount, String group, Map<Character, Ingredient> keys, Map<String, ICriterionInstance> criteria) {
        super(registry, result, resultCount, group, keys, criteria);
        this.builder = null;
    }

    private SingleItemRecipeBuilder builder;

    @Override
    public IStoneCutterRecipeBuilder withTag(CompoundNBT tag) {
        throw new UnsupportedOperationException(); //Unfortunately
    }

    @Override
    public IStoneCutterRecipeBuilder withOutput(Character input) {
        return withOutput(getIngredient(input));
    }

    @Override
    public IStoneCutterRecipeBuilder withOutput(Tag<Item> input) {
        return withOutput(Ingredient.fromTag(input));
    }

    @Override
    public IStoneCutterRecipeBuilder withOutput(IItemProvider input) {
        return withOutput(Ingredient.fromItems(input));
    }

    @Override
    public IStoneCutterRecipeBuilder withOutput(Ingredient input) {
        if (builder != null) {
            throw new IllegalStateException("Output already defined");
        }
        builder = SingleItemRecipeBuilder.stonecuttingRecipe(input, getResult(), resultCount);
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
