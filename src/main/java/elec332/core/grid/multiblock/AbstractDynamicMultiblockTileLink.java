package elec332.core.grid.multiblock;

import elec332.core.grid.DefaultTileEntityLink;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 1-8-2020
 */
public abstract class AbstractDynamicMultiblockTileLink<T extends TileEntity, M extends AbstractDynamicMultiblock<T, M, L>, L extends AbstractDynamicMultiblockTileLink<T, M, L>> extends DefaultTileEntityLink<T> {

    protected AbstractDynamicMultiblockTileLink(T tile) {
        super(tile);
    }

    private M grid;

    @Nullable
    public M getGrid() {
        return grid;
    }

    void setGrid(M grid) {
        this.grid = grid;
        setGridToTile(grid);
    }

    protected abstract void setGridToTile(@Nullable M grid);

}
