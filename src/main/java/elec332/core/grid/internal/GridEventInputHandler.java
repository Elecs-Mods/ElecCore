package elec332.core.grid.internal;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.grid.IStructureWorldEventHandler;
import elec332.core.handler.ElecCoreRegistrar;
import elec332.core.world.DimensionCoordinate;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

/**
 * Created by Elec332 on 23-7-2016.
 */
public enum GridEventInputHandler {

    INSTANCE;

    GridEventInputHandler() {
        this.bud = new ArrayDeque<>();
        this.chunkAdd = Maps.newHashMap();
        this.chunkRemove = Maps.newHashMap();
        this.notify = new ArrayDeque<>();
        this.cachedHandlers = Lists.newArrayList();
    }

    private List<IStructureWorldEventHandler> cachedHandlers;
    private final Queue<DimensionCoordinate> bud, notify;
    private final Map<IStructureWorldEventHandler, Set<DimensionCoordinate>> chunkAdd, chunkRemove;


    public void worldBlockUpdate(IWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
        if (!world.isRemote() && (newState.getBlock().hasTileEntity(newState)) || oldState.getBlock().hasTileEntity(oldState)) {
            bud.add(new DimensionCoordinate(world, pos));
        }
    }

    /**
     * Gets called when a block tells its neighbors that its state has changed.
     *
     * @param world The world
     * @param pos   The pos of the block that changed state
     * @param state The new state
     */
    public void onBlockNotify(IWorld world, BlockPos pos, BlockState state) {
        if (!world.isRemote()) {
            if (!state.getBlock().hasTileEntity(state)) {
                return;
            }
            DimensionCoordinate dimCoord = new DimensionCoordinate(world, pos);
            /*

            Issue #114, bud.contains causes severe performance impact
            Dont check for contains, but remove all bud entries in notify list later

            if (bud.contains(dimCoord)) {
                return;
            }

            */
            notify.add(dimCoord);
        }
    }

    public void chunkLoad(Chunk chunk) {
        if (!chunk.getWorld().isRemote) {
            for (Map.Entry<IStructureWorldEventHandler, Set<DimensionCoordinate>> entry : chunkAdd.entrySet()) {
                for (TileEntity tile : chunk.getTileEntityMap().values()) {
                    if (entry.getKey().isValidObject(tile)) {
                        entry.getValue().add(DimensionCoordinate.fromTileEntity(tile));
                    }
                }
            }
        }
    }

    public void chunkUnLoad(Chunk chunk) {
        if (!chunk.getWorld().isRemote) {
            for (Map.Entry<IStructureWorldEventHandler, Set<DimensionCoordinate>> entry : chunkRemove.entrySet()) {
                for (TileEntity tile : chunk.getTileEntityMap().values()) {
                    if (entry.getKey().isValidObject(tile)) {
                        entry.getValue().add(DimensionCoordinate.fromTileEntity(tile));
                    }
                }
            }
        }
    }

    public void tickEnd() { //We sometimes seem to run into concurrency issues, hence the Queue's
        List<DimensionCoordinate> l = Lists.newArrayList();
        DimensionCoordinate c;
        while ((c = this.bud.poll()) != null) {
            l.add(c);
        }
        Set<DimensionCoordinate> bud = ImmutableSet.copyOf(l);
        l.clear();
        while ((c = this.notify.poll()) != null) {
            l.add(c);
        }
        l.removeAll(bud); //Issue #114, better way of keeping notify list clean of bud entries
        Set<DimensionCoordinate> notify = ImmutableSet.copyOf(l);
        for (IStructureWorldEventHandler gridHandler : cachedHandlers) {
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
        //notify.clear();
        //bud.clear();
    }

    public void worldUnload(World world) {
        if (!world.isRemote()) {
            for (IStructureWorldEventHandler gridHandler : cachedHandlers) {
                gridHandler.worldUnload(world);
            }
        }
    }

    public void reloadHandlers() {
        cachedHandlers.clear();
        notify.clear();
        bud.clear();
        chunkAdd.clear();
        chunkRemove.clear();
        cachedHandlers.addAll(ElecCoreRegistrar.GRIDHANDLERS.getAllRegisteredObjects());
        for (IStructureWorldEventHandler agh : cachedHandlers) {
            chunkAdd.put(agh, Sets.newHashSet());
            chunkRemove.put(agh, Sets.newHashSet());
        }
    }

}
