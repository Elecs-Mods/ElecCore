package elec332.core.world.location;

import elec332.core.minetweaker.MineTweakerHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 26-7-2015.
 */
public final class BlockData implements IBlockDataEqualiser{

    public BlockData(Block block){
        this(block, 0);
    }

    public BlockData(Block block, int meta){
        this.block = block;
        this.meta = meta;
    }

    public final Block block;
    public final int meta;

    public ItemStack toItemStack(){
        return new ItemStack(block, 1, meta);
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
        return obj instanceof BlockData && blocksEqual((BlockData) obj);
    }

    @Override
    public boolean blocksEqual(BlockData blockData) {
        return (blockData.block == block || blockData.block == Blocks.air && block == null || blockData.block == null && block == Blocks.air) && checkMeta(blockData);
    }

}
