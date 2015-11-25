package elec332.core.world.location;

import elec332.core.minetweaker.MineTweakerHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 26-7-2015.
 */
public final class BlockStateWrapper implements IBlockDataEqualiser {

    public BlockStateWrapper(Block block){
        this(block, 0);
    }

    public BlockStateWrapper(IBlockState state){
        this(state.getBlock(), WorldHelper.getBlockMeta(state));
    }

    public BlockStateWrapper(Block block, int meta){
        this.block = block;
        this.meta = meta;
    }

    public final Block block;
    public final int meta;

    public ItemStack toItemStack(){
        return new ItemStack(block, 1, meta);
    }

    public IBlockState getBlockState(){
        return block.getStateFromMeta(meta);
    }

    @Override
    public String toString() {
        return MineTweakerHelper.getItemRegistryName(new ItemStack(block)) + ":" + hashCode();
    }

    @Override
    public int hashCode() {
        return meta;
    }

    private boolean checkMeta(Object obj){
        return obj.hashCode() == hashCode() || hashCode() == OreDictionary.WILDCARD_VALUE || obj.hashCode() == OreDictionary.WILDCARD_VALUE;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockStateWrapper && blocksEqual((BlockStateWrapper) obj);
    }

    @Override
    public boolean blocksEqual(BlockStateWrapper blockData) {
        return (blockData.block == block || blockData.block == Blocks.air && block == null || blockData.block == null && block == Blocks.air) && checkMeta(blockData);
    }

}
