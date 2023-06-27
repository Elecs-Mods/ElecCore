package elec332.core.data;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import elec332.core.data.loottable.AbstractBlockLootTables;
import elec332.core.data.loottable.AbstractEntityLootTables;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 14-7-2020
 */
public abstract class AbstractLootTableProvider extends LootTableProvider {

    public AbstractLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        this.tables = Maps.newHashMap();
    }

    private final Map<LootParameterSet, Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>> tables;

    protected abstract void registerLootTables();

    protected void addBlockLootTable(AbstractBlockLootTables lootTable) {
        addLootTable(LootParameterSets.BLOCK, () -> lootTable);
    }

    protected void addEntityLootTable(AbstractEntityLootTables lootTable) {
        addLootTable(LootParameterSets.ENTITY, () -> lootTable);
    }

    protected void addLootTable(LootParameterSet type, Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> tableBuilder) {
        Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> current = tables.get(type);
        Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> reg;
        if (current != null) {
            reg = () -> current.get().andThen(tableBuilder.get());
        } else {
            reg = tableBuilder;
        }
        tables.put(type, reg);
    }

    @Nonnull
    @Override
    protected final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        if (tables.isEmpty()) {
            registerLootTables();
        }
        return tables.entrySet().stream().map(e -> new Pair<>(e.getValue(), e.getKey())).collect(Collectors.toList());
    }

    @Override
    protected void validate(@Nonnull Map<ResourceLocation, LootTable> map, @Nonnull ValidationTracker validationtracker) {
    }

}
