package elec332.core.grid;

import elec332.core.world.DimensionCoordinate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Set;

/**
 * Created by Elec332 on 28-10-2016.
 */
public interface IStructureWorldEventHandler {

    public void checkNotifyStuff(Set<DimensionCoordinate> updates);

    public void checkBlockUpdates(Set<DimensionCoordinate> updates);

    public void checkChunkUnload(Set<DimensionCoordinate> updates);

    public void checkChunkLoad(Set<DimensionCoordinate> updates);

    public void worldUnload(World world);

    public void tick();

    public boolean isValidObject(TileEntity tile);

}
