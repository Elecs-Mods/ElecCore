package elec332.core.multiblock.dynamic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import elec332.core.util.BlockLoc;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Elec332 on 9-8-2015.
 */
public abstract class AbstractDynamicMultiBlock<A extends AbstractDynamicMultiBlockWorldHolder<A, M>, M extends AbstractDynamicMultiBlock<A, M>> {

    public AbstractDynamicMultiBlock(TileEntity tile, A worldHolder){
        if (tile == null || !worldHolder.isTileValid(tile) || !(tile instanceof IDynamicMultiBlockTile) || tile.getWorldObj() == null)
            throw new IllegalArgumentException("Invalid tile!");
        this.identifier = UUID.randomUUID();
        this.hash = new Random().nextInt(Integer.MAX_VALUE);
        this.world = tile.getWorldObj();
        this.allLocations = Lists.newArrayList();
        this.allLocations.add(new BlockLoc(tile));
    }

    protected final World world;
    private final int hash;
    private final UUID identifier;
    protected List<BlockLoc> allLocations;

    public void tick(){
    }

    public final List<BlockLoc> getAllLocations() {
        return ImmutableList.copyOf(allLocations);
    }

    protected void mergeWith(M multiBlock){
        this.allLocations.addAll(multiBlock.allLocations);
    }

    protected void onTileRemoved(TileEntity tile){
    }

    protected void invalidate(){
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && ((AbstractDynamicMultiBlock)obj).identifier.equals(identifier) && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
