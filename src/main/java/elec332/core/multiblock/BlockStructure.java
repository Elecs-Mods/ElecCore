package elec332.core.multiblock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import elec332.core.world.location.BlockData;

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
        this.structure = new BlockData[length][width][height];
        this.allBlocks = Lists.newArrayList();
        this.blockTypes = Lists.newArrayList();
        Map<BlockData, Integer> allBlocks = Maps.newHashMap();
        for (int l = 0; l < length; l++) {
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    BlockData blockData = data.getBlockAtPos(l, w, h);
                    if (blockData != null && blockData.block != null) {
                        structure[l][w][h] = blockData;
                        if (allBlocks.get(blockData) == null)
                            allBlocks.put(blockData, 0);
                        int i = allBlocks.get(blockData);
                        i++;
                        allBlocks.remove(blockData);
                        allBlocks.put(blockData, i);
                    } else if (blockData != null){
                        structure[l][w][h] = blockData;
                    } else {
                        structure[l][w][h] = new BlockData(null);
                    }
                }
            }
        }
        for (BlockData blockData : allBlocks.keySet()){
            int i = allBlocks.get(blockData);
            ItemStack stack = new ItemStack(blockData.block, i, blockData.meta);
            this.allBlocks.add(stack);
            this.blockTypes.add(blockData);
        }
    }

    private BlockData[][][] structure;
    private List<ItemStack> allBlocks;
    private List<BlockData> blockTypes;
    private final int hn;
    private final int length;
    private final int width;
    private final int height;

    public static interface IStructureFiller{
        public BlockData getBlockAtPos(int length, int width, int height);
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

    public BlockData[][][] getStructure() {
        return structure;
    }

    public List<ItemStack> getAllBlocks() {
        return allBlocks;
    }

    public List<BlockData> getBlockTypes() {
        return blockTypes;
    }

    protected int getHn() {
        return hn;
    }
}
