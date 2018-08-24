package elec332.core.grid;

import elec332.core.world.DimensionCoordinate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 1-8-2016.
 */
public class DefaultTileEntityLink implements ITileEntityLink {

    protected DefaultTileEntityLink(TileEntity tile) {
        this.tile = tile;
        this.coord = DimensionCoordinate.fromTileEntity(tile);
    }

    protected final TileEntity tile;
    protected final DimensionCoordinate coord;

    @Nullable
    @Override
    public TileEntity getTileEntity() {
        return tile;
    }

    @Nonnull
    @Override
    public DimensionCoordinate getPosition() {
        return coord;
    }

    //Capability tile link-through

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return hasCachedCapability(capability, facing) || coord.isLoaded() && tile != null && tile.hasCapability(capability, facing);
    }

    protected boolean hasCachedCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        T t = getCachedCapability(capability, facing);
        if (t != null) {
            return t;
        }
        if (tile == null) {
            return null;
        }
        return tile.getCapability(capability, facing);
    }

    @Nullable
    public <T> T getCachedCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }

}
