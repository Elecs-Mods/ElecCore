package elec332.core.grid;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.world.DimensionCoordinate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.OptionalCapabilityInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
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
    protected final Map<Capability<?>, EnumMap<EnumFacing, OptionalCapabilityInstance<?>>> capCache;

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
    public <T> OptionalCapabilityInstance<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        Map<EnumFacing, OptionalCapabilityInstance<?>> capC1 = capCache.computeIfAbsent(cap, c -> Maps.newEnumMap(EnumFacing.class));
        if (capC1.containsKey(side)) {
            OptionalCapabilityInstance<?> cret = capC1.get(side);
            if (cret.isPresent()) {
                return cret.cast();
            }
        }
        OptionalCapabilityInstance<?> ret;
        if (coord.isLoaded() && tile != null) {
            ret = Preconditions.checkNotNull(tile.getCapability(cap, side));
        } else {
            ret = OptionalCapabilityInstance.empty();
        }
        capC1.put(side, ret);
        return ret.cast();
    }

}
