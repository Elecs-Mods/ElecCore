package elec332.core.grid.multiblock;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import elec332.core.grid.AbstractGridHandler;
import elec332.core.world.DimensionCoordinate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 1-8-2020
 */
public class DynamicMultiblockGridHandler<T extends TileEntity, M extends AbstractDynamicMultiblock<T, M, L>, L extends AbstractDynamicMultiblockTileLink<T, M, L>> extends AbstractGridHandler<L> {

    public DynamicMultiblockGridHandler(Predicate<TileEntity> filter, Function<TileEntity, L> wrapper, Supplier<M> gridCreator) {
        this.filter = filter;
        this.wrapper = wrapper;
        this.gridCreator = gridCreator;
        this.grids = Sets.newHashSet();
    }

    private final Predicate<TileEntity> filter;
    private final Function<TileEntity, L> wrapper;
    private final Supplier<M> gridCreator;
    private final Set<M> grids;

    @Override
    protected void onObjectRemoved(L o, Set<DimensionCoordinate> allUpdates) {
        DimensionCoordinate coord = o.getPosition();
        M grid = o.getGrid();
        if (grid == null) {
            removeObject(coord);
            return;
        }
        for (L o2 : grid.getComponents()) {
            o2.setGrid(null);
            grid.onComponentRemoved(o2);
            if (!allUpdates.contains(o2.getPosition()) && o != o2) {
                add.add(o2.getPosition());
            }
        }
        grid.invalidate();
        grids.remove(grid);
    }

    @Override
    protected void internalAdd(L o) {
        DimensionCoordinate coord = o.getPosition();
        BlockPos pos = coord.getPos();
        o.setGrid(newGrid(o));
        for (Direction facing : Direction.values()) {
            L ttl = getDim(coord).get(pos.offset(facing));
            if (ttl != null) {
                M grid = ttl.getGrid();
                if (grid != null && grid != o.getGrid() && Preconditions.checkNotNull(o.getGrid()).canMerge(grid)) {
                    o.getGrid().mergeWith(grid);
                    grid.invalidate();
                    grids.remove(grid);
                }
            }
        }
    }

    @Override
    public void tick() {
        grids.forEach(M::tick);
    }

    @Override
    public boolean isValidObject(TileEntity tile) {
        return filter.test(tile);
    }

    @Override
    protected L createNewObject(TileEntity tile) {
        return wrapper.apply(tile);
    }

    private M newGrid(L link) {
        M ret = Preconditions.checkNotNull(gridCreator.get());
        ret.addComponent(link);
        grids.add(ret);
        return ret;
    }

}
