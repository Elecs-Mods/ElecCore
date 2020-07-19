package elec332.core.data.loottable;

import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.IItemProvider;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 16-7-2020
 */
public abstract class AbstractBlockLootTables extends BlockLootTables {

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

    static {

    }

}
