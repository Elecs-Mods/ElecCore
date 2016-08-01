package elec332.core.grid.v2;

import com.google.common.collect.Sets;
import elec332.core.world.DimensionCoordinate;
import elec332.core.world.PositionedObjectHolder;
import elec332.core.world.WorldHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Elec332 on 1-8-2016.
 */
public abstract class AbstractGridHandler<T extends ITileEntityLink> {

    public AbstractGridHandler(){
        this.objects = new Int2ObjectArrayMap<PositionedObjectHolder<T>>();
        this.extraUnload = Sets.newHashSet();
        this.changeCheck = Sets.newHashSet();
        this.add = Sets.newHashSet();
    }

    protected final Int2ObjectMap<PositionedObjectHolder<T>> objects;
    protected final Set<DimensionCoordinate> extraUnload, changeCheck, add;

    //Block changed
    @SuppressWarnings("all")
    public void checkNotifyStuff(Set<DimensionCoordinate> updates){
        for (DimensionCoordinate dimCoord : updates) {
            T o = getObject(dimCoord);
            if (o == null){
                continue;
            }
            TileEntity tile = dimCoord.getTileEntity();
            if (tile == null) {
                if (o != null) {
                    //o.clearTile(); //Just in case
                    extraUnload.add(dimCoord);
                }
                //Nothing to do, no tile and no energy object.
                return;
            } else {
                if (!o.getPosition().isLoaded()){
                    throw new IllegalStateException(); //Something has gone terribly wrong somewhere...
                }
                if (o.hasChanges()){
                    changeCheck.add(dimCoord);
                    return;
                }
                //Insignificant change, nothing more to do.
            }
        }
    }

    //Block added/removed (most likely atleast)
    public void checkBlockUpdates(Set<DimensionCoordinate> updates){
        for (DimensionCoordinate dimCoord : updates){
            TileEntity tile = dimCoord.getTileEntity();
            T o = getObject(dimCoord);
            if (o == null && tile == null){
                return;
            }
            if (o == null && isValidObject(tile)){
                add.add(dimCoord);
            }
            if (o != null && tile == null){
                extraUnload.add(dimCoord);
            }
            if (o != null && isValidObject(tile)){
                if (o.hasChanges()){
                    changeCheck.add(dimCoord);
                }
            }
        }
    }

    public void worldUnload(World world){
        PositionedObjectHolder<T> worldObjects = objects.get(WorldHelper.getDimID(world));
        if (worldObjects != null) {
            Set<DimensionCoordinate> unload = Sets.newHashSet();
            for (ChunkPos chunkPos : worldObjects.getChunks()) {
                for (T o : worldObjects.getObjectsInChunk(chunkPos).values()) {
                    unload.add(o.getPosition());
                }
            }
            unloadObjects_Internal(unload);
        }
    }

    public void checkChunkUnload(Set<DimensionCoordinate> updates){
        updates.addAll(extraUnload);
        updates.addAll(changeCheck);
        unloadObjects_Internal(updates);
        extraUnload.clear();
    }

    protected void unloadObjects_Internal(Set<DimensionCoordinate> updates){
        Set<DimensionCoordinate> updates_ = Collections.unmodifiableSet(updates);
        for (DimensionCoordinate dimCoord : updates){
            T o = getObject(dimCoord);
            if (o == null){
                System.out.println("????_-3"); //???
                continue;
            }
            onObjectRemoved(o, updates_);
            removeObject(dimCoord);
        }
    }

    protected abstract void onObjectRemoved(T o, Set<DimensionCoordinate> allUpdates);

    public void checkChunkLoad(Set<DimensionCoordinate> updates){
        Set<DimensionCoordinate> oldUpdates = Sets.newHashSet(updates);
        updates.addAll(add);
        updates.addAll(changeCheck);
        for (DimensionCoordinate dimCoord : updates){
            TileEntity tile = dimCoord.getTileEntity();
            if (tile == null || !isValidObject(tile)){
                continue;
            }
            T o = getObject(dimCoord);
            if (o != null){
                if (oldUpdates.contains(dimCoord)) {
                    throw new IllegalStateException();
                }
            } else {
                o = createNewObject(tile);
                objects.get(WorldHelper.getDimID(tile.getWorld())).put(o, tile.getPos());
                o.hasChanges(); //Set initial data
            }
            internalAdd(o);
        }
        add.clear();
        changeCheck.clear();
    }

    protected abstract void internalAdd(T o);

    public abstract void tick();

    public abstract boolean isValidObject(TileEntity tile);

    protected abstract T createNewObject(TileEntity tile);

    protected void removeObject(DimensionCoordinate dimensionCoordinate){
        getDim(dimensionCoordinate).remove(dimensionCoordinate.getPos());
    }

    protected T getObject(DimensionCoordinate dimensionCoordinate){
        return getDim(dimensionCoordinate).get(dimensionCoordinate.getPos());
    }

    protected PositionedObjectHolder<T> getDim(DimensionCoordinate dimensionCoordinate){
        PositionedObjectHolder<T> ret = objects.get(dimensionCoordinate.getDimension());
        if (ret == null){
            ret = new PositionedObjectHolder<>();
            objects.put(dimensionCoordinate.getDimension(), ret);
        }
        return ret;
    }

}
