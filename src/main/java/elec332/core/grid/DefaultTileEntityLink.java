package elec332.core.grid;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.world.DimensionCoordinate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by Elec332 on 1-8-2016.
 * <p>
 * Default tile wrapper for e.g. grids
 */
public class DefaultTileEntityLink implements ITileEntityLink {

    protected DefaultTileEntityLink(TileEntity tile) {
        this.tile = tile;
        this.coord = DimensionCoordinate.fromTileEntity(tile);
        this.capCache = Maps.newIdentityHashMap();
    }

    protected final TileEntity tile;
    protected final DimensionCoordinate coord;
    protected final Map<Capability<?>, Map<EnumFacing, LazyOptional<?>>> capCache;

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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        if (!coord.isLoaded() || tile == null) {
            return LazyOptional.empty();
        }
        Map<EnumFacing, LazyOptional<?>> capC1 = capCache.computeIfAbsent(cap, c -> Maps.newIdentityHashMap());
        if (capC1.containsKey(side)) {
            LazyOptional<?> cret = capC1.get(side);
            if (cret.isPresent()) {
                return cret.cast();
            }
        }
        LazyOptional<?> ret;
        //if (coord.isLoaded()) {
        ret = Preconditions.checkNotNull(tile.getCapability(cap, side));
        //} else {
        //    ret = LazyOptional.empty();
        //}
        capC1.put(side, ret);
        return ret.cast();
    }

}
