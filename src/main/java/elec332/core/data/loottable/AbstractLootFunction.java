package elec332.core.data.loottable;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 15-8-2020
 */
public abstract class AbstractLootFunction extends LootFunction {

    protected AbstractLootFunction(ILootCondition[] conditionsIn) {
        super(conditionsIn);
        requiredParameters = Sets.newHashSet();
        setRequiredParameters(requiredParameters::add);
    }

    private final Set<LootParameter<?>> requiredParameters;

    @Nonnull
    @Override
    protected final ItemStack doApply(@Nonnull ItemStack stack, @Nonnull LootContext context) {
        return applyFunction(stack, context);
    }

    @Nonnull
    protected abstract ItemStack applyFunction(@Nonnull ItemStack stack, @Nonnull LootContext context);

    protected abstract void setRequiredParameters(Consumer<LootParameter<?>> requires);

    @Nonnull
    @Override
    public final Set<LootParameter<?>> getRequiredParameters() {
        return requiredParameters;
    }

    public static abstract class Serializer<T extends LootFunction> extends LootFunction.Serializer<T> {

        public Serializer(ResourceLocation name, Class<T> type) {
            super(name, type);
        }

        @Nonnull
        @Override
        public final T deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull ILootCondition[] conditionsIn) {
            return deserializeObject(object, deserializationContext, conditionsIn);
        }

        @Nonnull
        public abstract T deserializeObject(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull ILootCondition[] conditions);

    }

    public static abstract class Builder<T extends LootFunction.Builder<T>> extends LootFunction.Builder<T> {

        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        protected T doCast() {
            return (T) this;
        }

        @Nonnull
        @Override
        public final ILootFunction build() {
            return buildFunction();
        }

        @Nonnull
        public abstract ILootFunction buildFunction();

    }

    public static class SimpleSerializer<T extends LootFunction> extends Serializer<T> {

        public SimpleSerializer(ResourceLocation name, Class<T> type, Function<ILootCondition[], T> constructor) {
            super(name, type);
            this.supplier = constructor;
        }

        private final Function<ILootCondition[], T> supplier;

        @Nonnull
        @Override
        public T deserializeObject(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull ILootCondition[] conditions) {
            return supplier.apply(conditions);
        }

    }

    public static class SimpleBuilder<T extends LootFunction> extends Builder<SimpleBuilder<T>> {

        public SimpleBuilder(Function<ILootCondition[], T> supplier) {
            this.supplier = supplier;
        }

        private final Function<ILootCondition[], T> supplier;

        @Nonnull
        @Override
        public ILootFunction buildFunction() {
            return supplier.apply(getConditions());
        }

    }

}
