package elec332.core.grid.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

/**
 * Created by Elec332 on 6-3-2016.
 */
public interface ITileData {

    public BlockPos getPos();

    /**
     * Yes, this object is supposed to have a DIRECT reference to the tile.
     *
     * @return The tile
     */
    public TileEntity getTile();

}
