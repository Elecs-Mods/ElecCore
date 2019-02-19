package elec332.core.grid;

import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.world.DimensionCoordinate;
import elec332.core.world.posmap.DefaultMultiWorldPositionedObjectHolder;
import elec332.core.world.posmap.IMultiWorldPositionedObjectHolder;
import elec332.core.world.posmap.PositionedObjectHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 1-8-2016.
 * <p>
 * An abstract implementation of {@link IStructureWorldEventHandler} that can be used for
 * dynamic grids (that span multiple worlds)
 */
public abstract class AbstractGridHandler<T extends IPositionable> implements IStructureWorldEventHandler {

    public AbstractGridHandler() {
        this.objectsInternal = getWorldPosObjHolder();
        this.objectsInternal.get().addCreateCallback(new Consumer<PositionedObjectHolder<T, T>>() {

            @Override
            public void accept(PositionedObjectHolder<T, T> obj) {
                for (PositionedObjectHolder.ChangeCallback<T> callback : changeCallbacks) {
                    obj.registerCallback(callback);
                }
            }

        });
        this.extraUnload = Sets.newHashSet();
        this.changeCheck = Sets.newHashSet();
        this.add = Sets.newHashSet();
        this.changeCallbacks = Sets.newHashSet();
        registerChangeCallback(new PositionedObjectHolder.ChangeCallback<T>() {
            @Override
            public void onChange(T objectU, BlockPos pos, boolean add) {
                if (objectU == null) {
                    return;
                }
                if (!(objectU instanceof ITileEntityLink)) {
                    return;
                }
                ITileEntityLink object = (ITileEntityLink) objectU;
                Class type = object.getInformationType();
                if (type == null) {
                    return;
                }
                TileEntity tile = object.getTileEntity();
                if (tile != null) {
                    for (Field field : tile.getClass().getDeclaredFields()) {
                        if (field.isAnnotationPresent(GridInformation.class)) {
                            Class clazz = field.getAnnotation(GridInformation.class).value();
                            if (clazz == type) {
                                if (!field.getType().isAssignableFrom(type)) {
                                    throw new IllegalArgumentException();
                                }
                                Object o = add ? object.getInformation() : null;
                                if (o != null && !type.isAssignableFrom(o.getClass())) {
                                    throw new IllegalArgumentException();
                                }
                                try {
                                    field.setAccessible(true);
                                    field.set(tile, o);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private final Supplier<IMultiWorldPositionedObjectHolder<T, T>> objectsInternal;
    private final Set<PositionedObjectHolder.ChangeCallback<T>> changeCallbacks;
    protected final Set<DimensionCoordinate> extraUnload, changeCheck, add;
    protected boolean removeWarningOverride = false;

    protected Supplier<IMultiWorldPositionedObjectHolder<T, T>> getWorldPosObjHolder() {
        return DefaultMultiWorldPositionedObjectHolder.create();
    }

    protected final Map<Integer, PositionedObjectHolder<T, T>> getObjects() {
        return objectsInternal.get().getUnModifiableView();
    }

    //Block changed
    @SuppressWarnings("all")
    @Override
    public void checkNotifyStuff(Set<DimensionCoordinate> updates) {
        for (DimensionCoordinate dimCoord : updates) {
            T o = getObject(dimCoord);
            if (o == null) {
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
                if (!o.getPosition().isLoaded()) {
                    throw new IllegalStateException(); //Something has gone terribly wrong somewhere...
                }
                if (o.hasChanged()) {
                    changeCheck.add(dimCoord);
                    return;
                }
                //Insignificant change, nothing more to do.
            }
        }
    }

    //Block added/removed (most likely atleast)
    @Override
    public void checkBlockUpdates(Set<DimensionCoordinate> updates) {
        for (DimensionCoordinate dimCoord : updates) {
            TileEntity tile = dimCoord.isLoaded() ? dimCoord.getTileEntity() : null;
            T o = getObject(dimCoord);
            if (o == null && tile == null) {
                continue;
            }
            if (o == null && isValidObject(tile)) {
                add.add(dimCoord);
            }
            if (o != null && tile == null) {
                extraUnload.add(dimCoord);
                continue;
            }
            if (o != null && isValidObject(tile)) {
                if (o.hasChanged()) {
                    changeCheck.add(dimCoord);
                }
            }
        }
    }

    @Override
    public void worldUnload(World world) {
        PositionedObjectHolder<T, T> worldObjects = objectsInternal.get().get(world);
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

    @Override
    public void checkChunkUnload(Set<DimensionCoordinate> updates) {
        updates.addAll(extraUnload);
        updates.addAll(changeCheck);
        unloadObjects_Internal(updates);
        extraUnload.clear();
    }

    protected void unloadObjects_Internal(Set<DimensionCoordinate> updates) {
        Set<DimensionCoordinate> updates_ = Collections.unmodifiableSet(updates);
        for (DimensionCoordinate dimCoord : updates) {
            T o = getObject(dimCoord);
            if (o == null) {
                System.out.println("????_-3"); //???
                continue;
            }
            onObjectRemoved(o, updates_);
            removeObject(dimCoord);
        }
    }

    /**
     * Gets called when an object is about to be removed
     *
     * @param o          The object being removed
     * @param allUpdates All other objects being removed
     */
    protected abstract void onObjectRemoved(T o, Set<DimensionCoordinate> allUpdates);

    @Override
    public void checkChunkLoad(Set<DimensionCoordinate> updates) {
        Set<DimensionCoordinate> oldUpdates = Sets.newHashSet(updates);
        updates.addAll(add);
        updates.addAll(changeCheck);
        for (DimensionCoordinate dimCoord : updates) {
            TileEntity tile = dimCoord.getTileEntity();
            if (tile == null || !isValidObject(tile)) {
                continue;
            }
            T o = getObject(dimCoord);
            if (o != null) {
                if (oldUpdates.contains(dimCoord) && !ElecCore.suppressSpongeIssues && !removeWarningOverride) {
                    throw new IllegalStateException();
                }
            } else {
                o = createNewObject(tile);
                objectsInternal.get().get(tile.getWorld()).put(o, tile.getPos());
                o.hasChanged(); //Set initial data
            }
            internalAdd(o);
        }
        add.clear();
        changeCheck.clear();
    }

    /**
     * Gets called when a object has been added
     *
     * @param o The object being added
     */
    protected abstract void internalAdd(T o);

    /**
     * Ticks this handler
     */
    @Override
    public abstract void tick();

    /**
     * Checks whether the provided tile is a valid object for grid(s) handled by this handler
     * (Or: Whether this grid want to know about changes happening to it)
     *
     * @param tile The {@link TileEntity} being checked
     * @return Whether the provided tile is important to the structure
     */
    @Override
    public abstract boolean isValidObject(TileEntity tile);

    /**
     * Wraps a tile into a predefined type
     *
     * @param tile The tile being wrapped
     * @return The wrapped tile
     */
    protected abstract T createNewObject(TileEntity tile);

    protected void removeObject(DimensionCoordinate dimensionCoordinate) {
        getDim(dimensionCoordinate).remove(dimensionCoordinate.getPos());
    }

    protected T getObject(DimensionCoordinate dimensionCoordinate) {
        return getDim(dimensionCoordinate).get(dimensionCoordinate.getPos());
    }

    protected PositionedObjectHolder<T, T> getDim(DimensionCoordinate dimensionCoordinate) {
        return objectsInternal.get().getOrCreate(dimensionCoordinate.getDimension());
    }

    public void registerChangeCallback(PositionedObjectHolder.ChangeCallback<T> callback) {
        if (changeCallbacks.add(callback)) {
            for (PositionedObjectHolder<T, T> o : objectsInternal.get().getValues()) {
                o.registerCallback(callback);
            }
        }
    }

}
