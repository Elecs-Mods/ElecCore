package elec332.core.data.loottable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 16-7-2020
 */
public abstract class AbstractBlockLootTables extends BlockLootTables {

    public AbstractBlockLootTables() {
        this(null);
    }

    public AbstractBlockLootTables(String modId) {
        this.modId = modId;
        lootTables = Maps.newHashMap();
    }

    private final String modId;
    private final Map<ResourceLocation, LootTable.Builder> lootTables;

    @Override
    protected final void addTables() {
        registerBlockTables();
    }

    protected abstract void registerBlockTables();

    protected static void addImmuneToExplosions(IItemProvider block) {

    }

    protected void registerDropSelfLootTable(Supplier<Block> block) {
        registerDropSelfLootTable(block.get());
    }

    protected void registerEmptyLootTable(Block block) {
        registerLootTable(block, func_218482_a());
    }

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
    protected Iterable<Block> getKnownBlocks() {
        return Lists.newArrayList(super.getKnownBlocks())
                .stream()
                .filter(b -> Preconditions.checkNotNull(b.getRegistryName()).getNamespace().equals(modId))
                .collect(Collectors.toList());
    }

    @Override
    protected void registerLootTable(@Nonnull Block blockIn, @Nonnull LootTable.Builder table) {
        this.lootTables.put(blockIn.getLootTable(), table);
        super.registerLootTable(blockIn, table);
    }

}
