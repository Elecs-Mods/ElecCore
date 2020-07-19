package elec332.core.data.recipe;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.api.data.recipe.IRecipeBuilder;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 15-7-2020
 */
public abstract class AbstractRecipeBuilder<T extends IRecipeBuilder<T>> implements IRecipeBuilder<T> {

    public AbstractRecipeBuilder(Consumer<IFinishedRecipe> registry, IItemProvider result, int resultCount, String group, Map<Character, Ingredient> keys, Map<String, ICriterionInstance> criteria) {
        this.registry = registry;
        this.result = result.asItem();
        this.resultCount = resultCount;
        this.keys = Maps.newLinkedHashMap(keys);
        this.criteria = Maps.newHashMap(criteria);
        this.group = group;
        this.registeredKeys = Maps.newLinkedHashMap();
    }

    protected final int resultCount;
    private final Consumer<IFinishedRecipe> registry;
    private final Item result;
    private final String group;
    private final Map<Character, Ingredient> keys, registeredKeys;
    private final Map<String, ICriterionInstance> criteria;
    protected CompoundNBT tag;

    protected void register(IFinishedRecipe recipe) {
        registry.accept(recipe);
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T withTag(CompoundNBT tag) {
        this.tag = tag;
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T addCriterion(String name, ICriterionInstance criterion) {
        criteria.put(Preconditions.checkNotNull(name), Preconditions.checkNotNull(criterion));
        return (T) this;
    }

    protected Map<String, ICriterionInstance> getCriteria() {
        if (criteria.isEmpty()) {
            throw new IllegalStateException("No criteria registered!");
        }
        return criteria;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T key(Character symbol, Ingredient ingredient) {
        addKey(symbol, ingredient, keys);
        return (T) this;
    }

    public static void addKey(Character symbol, Ingredient ingredient, Map<Character, Ingredient> map) {
        if (map.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            map.put(symbol, ingredient);
        }
    }

    protected void maybeAdd(Character ch, BiConsumer<Character, Ingredient> registry) {
        if (ch != ' ' && !registeredKeys.containsKey(ch)) {
            Ingredient i = getIngredient(ch);
            registeredKeys.put(ch, i);
            registry.accept(ch, i);
        }
    }

    @Nonnull
    protected Ingredient getIngredient(Character key) {
        if (!this.keys.containsKey(key)) {
            throw new IllegalArgumentException(key + " is not a registered key");
        }
        return this.keys.get(key);
    }

    @Override
    public abstract void build(ResourceLocation id);

}
