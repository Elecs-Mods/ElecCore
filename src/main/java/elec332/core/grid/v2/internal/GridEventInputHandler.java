package elec332.core.grid.v2.internal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.grid.v2.AbstractGridHandler;
import elec332.core.main.ElecCoreRegistrar;
import elec332.core.world.DimensionCoordinate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 23-7-2016.
 */
public enum GridEventInputHandler {

    INSTANCE;

    GridEventInputHandler(){
        this.bud = Sets.newHashSet();
        this.chunkAdd = Maps.newHashMap();
        this.chunkRemove = Maps.newHashMap();
        this.notify = Sets.newHashSet();
        this.cachedHandlers = Lists.newArrayList();
    }

    private List<AbstractGridHandler> cachedHandlers;
    private final Set<DimensionCoordinate> bud, notify;
    private final Map<AbstractGridHandler, Set<DimensionCoordinate>> chunkAdd, chunkRemove;


    public void worldBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
        if (!world.isRemote && (newState.getBlock().hasTileEntity(newState)) || oldState.getBlock().hasTileEntity(oldState)) {
            bud.add(new DimensionCoordinate(world, pos));
        }
    }

    /**
     * Gets called when a block tells its neighbors that its state has changed.
     *
     * @param world The world
     * @param pos The pos of the block that changed state
     * @param state The new state
     */
    public void onBlockNotify(World world, BlockPos pos, IBlockState state){
        if (!world.isRemote) {
            if (!state.getBlock().hasTileEntity(state)) {
                return;
            }
            DimensionCoordinate dimCoord = new DimensionCoordinate(world, pos);
            if (bud.contains(dimCoord)) {
                return;
            }
            notify.add(dimCoord);
        }
    }

    public void chunkLoad(Chunk chunk){
        if (!chunk.getWorld().isRemote) {
            for (Map.Entry<AbstractGridHandler, Set<DimensionCoordinate>> entry : chunkAdd.entrySet()) {
                for (TileEntity tile : chunk.getTileEntityMap().values()) {
                    if (entry.getKey().isValidObject(tile)) {
                        entry.getValue().add(DimensionCoordinate.fromTileEntity(tile));
                    }
                }
            }
        }
    }

    public void chunkUnLoad(Chunk chunk){
        if (!chunk.getWorld().isRemote) {
            for (Map.Entry<AbstractGridHandler, Set<DimensionCoordinate>> entry : chunkRemove.entrySet()) {
                for (TileEntity tile : chunk.getTileEntityMap().values()) {
                    if (entry.getKey().isValidObject(tile)) {
                        entry.getValue().add(DimensionCoordinate.fromTileEntity(tile));
                    }
                }
            }
        }
    }

    public void tickEnd(){
        Set<DimensionCoordinate> bud = Sets.newHashSet(this.bud);
        Set<DimensionCoordinate> notify = Sets.newHashSet(this.notify);
        this.bud.clear();
        this.notify.clear();
        for (AbstractGridHandler gridHandler : cachedHandlers) {
            gridHandler.checkNotifyStuff(notify);
            gridHandler.checkBlockUpdates(bud);

            Set<DimensionCoordinate> chunkRemove, chunkRemove_ = this.chunkRemove.get(gridHandler);
            chunkRemove = Sets.newHashSet(chunkRemove_);
            chunkRemove_.clear();
            gridHandler.checkChunkUnload(chunkRemove);
            chunkRemove.clear();

            Set<DimensionCoordinate> chunkAdd, chunkAdd_ = this.chunkAdd.get(gridHandler);
            chunkAdd = Sets.newHashSet(chunkAdd_);
            chunkAdd_.clear();
            gridHandler.checkChunkLoad(chunkAdd);
            chunkAdd.clear();

            gridHandler.tick();
        }
        notify.clear();
        bud.clear();
    }

    public void worldUnload(World world){
        if (!world.isRemote){
            for (AbstractGridHandler gridHandler : cachedHandlers) {
                gridHandler.worldUnload(world);
            }
        }
    }

    public void reloadHandlers(){
        cachedHandlers.clear();
        notify.clear();
        bud.clear();
        chunkAdd.clear();
        chunkRemove.clear();
        cachedHandlers.addAll(ElecCoreRegistrar.GRIDS_V2.getAllRegisteredObjects());
        for (AbstractGridHandler agh : cachedHandlers){
            chunkAdd.put(agh, Sets.newHashSet());
            chunkRemove.put(agh, Sets.newHashSet());
        }
    }

}
