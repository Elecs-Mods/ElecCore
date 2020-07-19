package elec332.core.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.api.data.recipe.IGroupedRecipeManager;
import elec332.core.api.data.recipe.IRecipeBuilder;
import elec332.core.api.data.recipe.IRecipeType;
import elec332.core.api.data.recipe.impl.ICookingRecipeBuilder;
import elec332.core.api.data.recipe.impl.IShapedRecipeBuilder;
import elec332.core.api.data.recipe.impl.IShapelessRecipeBuilder;
import elec332.core.api.data.recipe.impl.IStoneCutterRecipeBuilder;
import elec332.core.data.recipe.AbstractRecipeBuilder;
import elec332.core.data.recipe.RecipeTypes;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 14-7-2020
 */
public abstract class AbstractRecipeProvider extends RecipeProvider {

    public AbstractRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected final void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        registerRecipes(new GroupedRecipeManager("", consumer, Maps.newHashMap(), Maps.newHashMap()));
        registerSpecialRecipes(consumer);
    }

    protected abstract void registerRecipes(IGroupedRecipeManager recipeBuilder);

    protected void registerSpecialRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
    }

    private static class GroupedRecipeManager implements IGroupedRecipeManager {

        private GroupedRecipeManager(String group, Consumer<IFinishedRecipe> registry, Map<Character, Ingredient> keys, Map<String, ICriterionInstance> criteria) {
            this.group = Preconditions.checkNotNull(group);
            this.registry = Preconditions.checkNotNull(registry);
            this.keys = Maps.newLinkedHashMap(keys);
            this.criteria = Maps.newHashMap(criteria);
        }

        private final String group;
        private final Consumer<IFinishedRecipe> registry;
        private final Map<Character, Ingredient> keys;
        private final Map<String, ICriterionInstance> criteria;

        @Override
        public void grouped(Consumer<IGroupedRecipeManager> groupedRecipes) {
            grouped(group, groupedRecipes);
        }

        @Override
        public void grouped(String group, Consumer<IGroupedRecipeManager> groupedRecipes) {
            IGroupedRecipeManager recipeManager = new GroupedRecipeManager(group, registry, keys, criteria);
            groupedRecipes.accept(recipeManager);
        }

        @Override
        public IShapedRecipeBuilder shapedRecipe(IItemProvider item, int count) {
            return customRecipe(RecipeTypes.SHAPED_RECIPE, item, count);
        }

        @Override
        public IShapelessRecipeBuilder shapelessRecipe(IItemProvider item, int count) {
            return customRecipe(RecipeTypes.SHAPELESS_RECIPE, item, count);
        }

        @Override
        public ICookingRecipeBuilder furnaceRecipe(IItemProvider item, int count) {
            return customRecipe(RecipeTypes.FURNACE_RECIPE, item, count);
        }

        @Override
        public ICookingRecipeBuilder blastFurnaceRecipe(IItemProvider item, int count) {
            return customRecipe(RecipeTypes.BLASTFURNACE_RECIPE, item, count);
        }

        @Override
        public ICookingRecipeBuilder smokingRecipe(IItemProvider item, int count) {
            return customRecipe(RecipeTypes.SMOKING_RECIPE, item, count);
        }

        @Override
        public ICookingRecipeBuilder campfireRecipe(IItemProvider item, int count) {
            return customRecipe(RecipeTypes.CAMPFIRE_RECIPE, item, count);
        }

        @Override
        public IStoneCutterRecipeBuilder stonecuttingRecipe(IItemProvider item, int count) {
            return customRecipe(RecipeTypes.STONECUTTING_RECIPE, item, count);
        }

        @Override
        public <T extends IRecipeBuilder<T>> T customRecipe(IRecipeType<T> type, IItemProvider item, int count) {
            return type.createBuilder(registry, Preconditions.checkNotNull(item), count, group, keys, criteria);
        }

        @Override
        public IGroupedRecipeManager addCriterion(String name, ICriterionInstance criterion) {
            criteria.put(Preconditions.checkNotNull(name), Preconditions.checkNotNull(criterion));
            return this;
        }

        @Override
        public IGroupedRecipeManager key(Character symbol, Ingredient ingredient) {
            AbstractRecipeBuilder.addKey(symbol, ingredient, keys);
            return this;
        }

    }

}
