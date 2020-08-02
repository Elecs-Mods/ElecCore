package elec332.core.grid.multiblock;

import com.google.common.base.Preconditions;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

/**
 * Created by Elec332 on 1-8-2020
 */
public class SimpleDynamicMultiblockTileLink<T extends TileEntity, M extends AbstractDynamicMultiblock<T, M, SimpleDynamicMultiblockTileLink<T, M>>> extends AbstractDynamicMultiblockTileLink<T, M, SimpleDynamicMultiblockTileLink<T, M>> {

    public static <T extends TileEntity, M extends AbstractDynamicMultiblock<T, M, SimpleDynamicMultiblockTileLink<T, M>>> SimpleDynamicMultiblockTileLink<T, M> wrap(T tile) {
        return new SimpleDynamicMultiblockTileLink<>(tile);
    }

    public SimpleDynamicMultiblockTileLink(T tile) {
        this(tile, (t, g) -> {
        });
    }

    public SimpleDynamicMultiblockTileLink(T tile, BiConsumer<T, M> gridSetter) {
        super(tile);
        this.gridSetter = Preconditions.checkNotNull(gridSetter);
    }

    private final BiConsumer<T, M> gridSetter;

    @Override
    protected void setGridToTile(@Nullable M grid) {
        gridSetter.accept(Preconditions.checkNotNull(getTileEntity()), grid);
    }

}
