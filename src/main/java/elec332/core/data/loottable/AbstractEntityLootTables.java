package elec332.core.data.loottable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 16-7-2020
 */
public abstract class AbstractEntityLootTables extends EntityLootTables {

    public AbstractEntityLootTables() {
        this(null);
    }

    public AbstractEntityLootTables(String modId) {
        this.modId = modId;
        lootTables = Maps.newHashMap();
    }

    private final String modId;
    private final Map<ResourceLocation, LootTable.Builder> lootTables;

    @Override
    protected final void addTables() {
        registerEntityTables();
    }

    protected abstract void registerEntityTables();

    @Override
    public void accept(@Nonnull BiConsumer<ResourceLocation, LootTable.Builder> registry) {
        if (modId == null) {
            addTables();
            lootTables.forEach(registry);
        } else {
            super.accept(registry);
        }
    }

    @Nonnull
    @Override
    protected Iterable<EntityType<?>> getKnownEntities() {
        return Lists.newArrayList(super.getKnownEntities())
                .stream()
                .filter(b -> Preconditions.checkNotNull(b.getRegistryName()).getNamespace().equals(modId))
                .collect(Collectors.toList());
    }

    @Override
    protected void registerLootTable(@Nonnull EntityType<?> type, @Nonnull LootTable.Builder table) {
        this.lootTables.put(type.getLootTable(), table);
        super.registerLootTable(type, table);
    }

}
