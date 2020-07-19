package elec332.core.data.recipe;

import elec332.core.api.data.recipe.impl.IShapedRecipeBuilder;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 15-7-2020
 */
public class ShapedRecipeBuilder extends AbstractCraftingRecipeBuilder<IShapedRecipeBuilder> implements IShapedRecipeBuilder {

    public ShapedRecipeBuilder(Consumer<IFinishedRecipe> registry, IItemProvider result, int resultCount, String group, Map<Character, Ingredient> keys, Map<String, ICriterionInstance> criteria) {
        super(registry, result, resultCount, group, keys, criteria);
        this.builder = net.minecraft.data.ShapedRecipeBuilder.shapedRecipe(result, resultCount);
    }

    private final net.minecraft.data.ShapedRecipeBuilder builder;

    @Override
    public ShapedRecipeBuilder patternLine(String pattern) {
        pattern.chars().forEach(c -> maybeAdd((char) c, builder::key));
        builder.patternLine(pattern);
        return this;
    }

    @Override
    public void build(ResourceLocation id) {
        builder.setGroup(getGroup());
        getCriteria().forEach(builder::addCriterion);
        builder.build(this::register, id);
    }

}
