package elec332.core.multiblock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.world.location.BlockStateWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 26-7-2015.
 */
public final class BlockStructure {

    public BlockStructure(int length, int width, int height, IStructureFiller data){
        this.hn = Math.max(length, Math.max(width, height));
        this.length = length;
        this.width = width;
        this.height = height;
        this.structure = new BlockStateWrapper[length][width][height];
        this.allBlocks = Lists.newArrayList();
        this.blockTypes = Lists.newArrayList();
        Map<BlockStateWrapper, Integer> allBlocks = Maps.newHashMap();
        for (int l = 0; l < length; l++) {
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    BlockStateWrapper blockData = data.getBlockAtPos(l, w, h);
                    if (blockData != null && blockData.block != null) {
                        structure[l][w][h] = blockData;
                        if (allBlocks.get(blockData) == null)
                            allBlocks.put(blockData, 0);
                        int i = allBlocks.get(blockData);
                        i++;
                        allBlocks.remove(blockData);
                        allBlocks.put(blockData, i);
                    } else /*if (blockData != null)*/{
                        structure[l][w][h] = blockData;
                    }/* else {
                        structure[l][w][h] = new BlockStateWrapper((Block)null);
                    }*/
                }
            }
        }
        for (BlockStateWrapper blockData : allBlocks.keySet()){
            int i = allBlocks.get(blockData);
            ItemStack stack = new ItemStack(blockData.block, i, blockData.meta);
            this.allBlocks.add(stack);
            this.blockTypes.add(blockData);
        }
    }

    private BlockStateWrapper[][][] structure;
    private List<ItemStack> allBlocks;
    private List<BlockStateWrapper> blockTypes;
    private final int hn;
    private final int length;
    private final int width;
    private final int height;

    public static interface IStructureFiller{
        public BlockStateWrapper getBlockAtPos(int length, int width, int height);
    }

    protected static interface IPositionCall{
        public void forPos(int length, int width, int height);
    }

    public BlockStructure newBlockStructureWithSameDimensions(IStructureFiller structureFiller){
        return new BlockStructure(length, width, height, structureFiller);
    }

    protected void startLoop(IPositionCall callable){
        for (int l = 0; l < length; l++) {
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    callable.forPos(l, w, h);
                }
            }
        }
    }

    public BlockStateWrapper[][][] getStructure() {
        return structure;
    }

    public List<ItemStack> getAllBlocks() {
        return allBlocks;
    }

    public List<BlockStateWrapper> getBlockTypes() {
        return blockTypes;
    }

    protected int getHn() {
        return hn;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
