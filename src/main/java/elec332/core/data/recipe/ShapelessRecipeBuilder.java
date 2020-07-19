package elec332.core.data.recipe;

import elec332.core.api.data.recipe.impl.IShapelessRecipeBuilder;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 15-7-2020
 */
public class ShapelessRecipeBuilder extends AbstractCraftingRecipeBuilder<IShapelessRecipeBuilder> implements IShapelessRecipeBuilder {

    public ShapelessRecipeBuilder(Consumer<IFinishedRecipe> registry, IItemProvider result, int resultCount, String group, Map<Character, Ingredient> keys, Map<String, ICriterionInstance> criteria) {
        super(registry, result, resultCount, group, keys, criteria);
        this.builder = net.minecraft.data.ShapelessRecipeBuilder.shapelessRecipe(result, resultCount);
    }

    private final net.minecraft.data.ShapelessRecipeBuilder builder;

    @Override
    public ShapelessRecipeBuilder addIngredient(Tag<Item> tag) {
        this.builder.addIngredient(tag);
        return this;
    }

    @Override
    public ShapelessRecipeBuilder addIngredient(IItemProvider item) {
        this.builder.addIngredient(item);
        return this;
    }

    @Override
    public ShapelessRecipeBuilder addIngredient(IItemProvider item, int quantity) {
        this.builder.addIngredient(item, quantity);
        return this;
    }

    @Override
    public ShapelessRecipeBuilder addIngredient(Ingredient ingredient) {
        this.builder.addIngredient(ingredient);
        return this;
    }

    @Override
    public ShapelessRecipeBuilder addIngredient(Ingredient ingredient, int quantity) {
        this.builder.addIngredient(ingredient, quantity);
        return this;
    }

    @Override
    public ShapelessRecipeBuilder addIngredient(Character ingredient) {
        this.builder.addIngredient(getIngredient(ingredient));
        return this;
    }

    @Override
    public ShapelessRecipeBuilder addIngredient(Character ingredient, int quantity) {
        this.builder.addIngredient(getIngredient(ingredient), quantity);
        return this;
    }

    @Override
    public void build(ResourceLocation id) {
        builder.setGroup(getGroup());
        getCriteria().forEach(builder::addCriterion);
        builder.build(this::register, id);
    }

}
