package elec332.core.grid;

import elec332.core.world.DimensionCoordinate;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.World;

import java.util.Set;

/**
 * Created by Elec332 on 28-10-2016.
 * <p>
 * Interface for handling dynamic multiblock structures, can span multiple dimensions
 */
public interface IStructureWorldEventHandler {

    /**
     * Gets called every tick, the set contains the {@link DimensionCoordinate}'s of all blocks that
     * notified one or multiple neighbors of a change last tick
     *
     * @param updates The blocks that have sent out one or more notifications to its neighbors that it has changed
     */
    public void checkNotifyStuff(Set<DimensionCoordinate> updates);

    /**
     * Gets called every tick, the set contains the {@link DimensionCoordinate}'s of all positions
     * that've had a block update last tick
     *
     * @param updates The blocks that have sent been updated last tick
     */
    public void checkBlockUpdates(Set<DimensionCoordinate> updates);

    /**
     * Gets called every tick, the set contains the {@link DimensionCoordinate}'s of all
     * {@link IStructureWorldEventHandler#isValidObject(TileEntity)} valid objects
     * that are in a chunk that has recently been unloaded
     *
     * @param updates The now unloaded positions
     */
    public void checkChunkUnload(Set<DimensionCoordinate> updates);

    /**
     * Gets called every tick, the set contains the {@link DimensionCoordinate}'s of all
     * {@link IStructureWorldEventHandler#isValidObject(TileEntity)} valid objects
     * that are in a chunk that has recently been loaded
     *
     * @param updates The now loaded positions
     */
    public void checkChunkLoad(Set<DimensionCoordinate> updates);

    /**
     * Gets called when a world unloads
     *
     * @param world The unloading world
     */
    public void worldUnload(World world);

    /**
     * Ticks this handler
     */
    public void tick();

    /**
     * Checks whether the provided tile is a valid object for this structure/structures handled by this handler
     * (Or: Whether this structure want to know about changes happening to it)
     *
     * @param tile The {@link TileEntity} being checked
     * @return Whether the provided tile is important to the structure
     */
    public boolean isValidObject(TileEntity tile);

}
