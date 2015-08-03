package elec332.core.multiblock;

import elec332.core.minetweaker.MineTweakerHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 26-7-2015.
 */
public final class BlockData {

    public BlockData(Block block){
        this(block, 0);
    }

    public BlockData(Block block, int meta){
        this.block = block;
        this.meta = meta;
    }

    public final Block block;
    public final int meta;

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
        return obj instanceof BlockData && ((BlockData) obj).block == block && checkMeta(obj);
    }
}
