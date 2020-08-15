package elec332.core.data.loottable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.item.AbstractItemBlock;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.CopyNbt;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 16-7-2020
 */
@StaticLoad
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
        if (IMMUNE_TO_EXPLOSIONS instanceof ImmutableSet) {
            IMMUNE_TO_EXPLOSIONS = Sets.newHashSet(IMMUNE_TO_EXPLOSIONS);
        }
        IMMUNE_TO_EXPLOSIONS.add(block.asItem());
    }

    protected void registerDropSelfLootTable(Supplier<? extends Block> block, UnaryOperator<LootTable.Builder> modifier) {
        Block b = block.get();
        this.registerLootTable(b, modifier.apply(dropping(b)));
    }

    protected void registerDropSelfLootTable(Supplier<? extends Block> block) {
        registerDropSelfLootTable(block.get());
    }

    protected void registerEmptyLootTable(Block block) {
        registerLootTable(block, func_218482_a());
    }

    protected void registerLootTable(Supplier<? extends Block> block, Function<Block, LootTable.Builder> factory) {
        this.registerLootTable(block.get(), factory);
    }

    protected void registerLootTable(Supplier<? extends Block> block, LootTable.Builder table) {
        registerLootTable(block.get(), table);
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

    public static LootFunction.Builder<?> copyAllTileTags() {
        return new AbstractLootFunction.SimpleBuilder<>(CopyAllTileNBT::new);
    }

    public static CopyNbt.Builder copyTileTags(String... tags) {
        CopyNbt.Builder ret = CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY);
        for (String s : tags) {
            ret.replaceOperation(s, AbstractItemBlock.TILE_DATA_TAG + "." + s);
        }
        return ret;
    }

    private static class CopyAllTileNBT extends AbstractLootFunction {

        private CopyAllTileNBT(ILootCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @Nonnull
        @Override
        protected ItemStack applyFunction(@Nonnull ItemStack stack, @Nonnull LootContext context) {
            TileEntity tile = Preconditions.checkNotNull(context.get(LootParameters.BLOCK_ENTITY));
            CompoundNBT tag = new CompoundNBT();
            tile.write(tag);
            stack.getOrCreateTag().put(AbstractItemBlock.TILE_DATA_TAG, tag);
            return stack;
        }

        @Override
        protected void setRequiredParameters(Consumer<LootParameter<?>> requires) {
            requires.accept(LootParameters.BLOCK_ENTITY);
        }

    }

    static {
        LootFunctionManager.registerFunction(new AbstractLootFunction.SimpleSerializer<>(new ResourceLocation("eleccore", "copy_all_tile_nbt"), CopyAllTileNBT.class, CopyAllTileNBT::new));
    }

}
