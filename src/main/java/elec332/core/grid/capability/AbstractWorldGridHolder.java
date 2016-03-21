package elec332.core.grid.capability;

import elec332.core.registry.IWorldRegistry;
import elec332.core.world.PositionedObjectHolder;
import elec332.core.world.SurroundingChecker;
import elec332.core.world.WorldHelper;
import mcmultipart.capabilities.MultipartCapabilityHelper;
import mcmultipart.event.PartEvent;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

/**
 * Created by Elec332 on 23-4-2015.
 */
@SuppressWarnings("unused")
public abstract class AbstractWorldGridHolder<T extends ITileData, O> implements IWorldRegistry, SurroundingChecker.IFacedSurroundingDataFactory<O>, SurroundingChecker.ISurroundingHandler<O> {

    public AbstractWorldGridHolder(World world){
        this.world = world;
        this.registeredTiles = new PositionedObjectHolder<T>();
        this.pending = new ArrayDeque<T>();
        this.surroundingChecker = new SurroundingChecker<O>(this, world, this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    protected final World world;
    private final Queue<T> pending;
    private final PositionedObjectHolder<T> registeredTiles;
    private final SurroundingChecker<O> surroundingChecker;

    @SubscribeEvent
    public final void partAdd(PartEvent.Add event){
        IMultipart multiPart = event.part;
        if (multiPart.getWorld().isRemote) {
            if (WorldHelper.getDimID(multiPart.getWorld()) != WorldHelper.getDimID(world)) {
                return;
            }
            BlockPos pos = multiPart.getPos();
            if (!WorldHelper.chunkLoaded(world, pos)) {
                throw new RuntimeException();
            }
            T t = getPowerTile(pos);
            TileEntity tile = WorldHelper.getTileAt(world, pos);
            if (isValidTile(tile)) {
                if (t == null) {
                    addTile(tile);
                } else {
                    resetPosAndSurroundings(pos);
                    onExtraMultiPartAdded(t, multiPart);
                }
            }
        }
    }

    @SubscribeEvent
    public final void partRemove(PartEvent.Remove event){
        IMultipart multiPart = event.part;
        if (multiPart.getWorld().isRemote) {
            if (WorldHelper.getDimID(multiPart.getWorld()) != WorldHelper.getDimID(world)) {
                return;
            }
            BlockPos pos = multiPart.getPos();
            T t = getPowerTile(pos);
            if (t != null){
                TileEntity tile = t.getTile();
                if(!isValidTile(tile)) {
                    removeTile(tile);
                } else {
                    resetPosAndSurroundings(pos);
                    onMultiPartRemoved(t, multiPart);
                }
            }
        }
    }

    @SubscribeEvent
    public final void chunkUnload(ChunkEvent.Unload event){
        World world = event.world;
        if (!world.isRemote && WorldHelper.getDimID(world) == WorldHelper.getDimID(this.world)) {
            ChunkCoordIntPair chunkPos = event.getChunk().getChunkCoordIntPair();
            if (registeredTiles.chunkExists(chunkPos)) {
                Collection<T> list = registeredTiles.getObjectsInChunk(chunkPos).values();
                if (!list.isEmpty()) {
                    onChunkUnload(list);
                }
            }
        }
    }

    @SubscribeEvent
    public final void chunkLoad(ChunkEvent.Load event){
        World world = event.world;
        if (!world.isRemote && WorldHelper.getDimID(world) == WorldHelper.getDimID(this.world)) {
            onChunkLoad(event.getChunk().getTileEntityMap().values());
        }
    }

    public final void addTile(TileEntity tile){
        if (!tile.getWorld().isRemote) {
            BlockPos tilePos = tile.getPos();
            if (WorldHelper.getDimID(world) != WorldHelper.getDimID(this.world)) {
                throw new IllegalArgumentException();
            }
            if (!WorldHelper.chunkLoaded(world, tilePos) || !isValidTile(tile)) {
                return;
            }
            if (!surroundingChecker.isChange(tilePos)){
                return;
            }
            if (getPowerTile(tilePos) != null){
                throw new IllegalStateException();
            }
            T powerTile = generate(tile);
            registeredTiles.put(powerTile, tilePos);
            surroundingChecker.addPosition(tilePos);
            resetPosAndSurroundings(tilePos);
            add(powerTile);
        }
    }

    public final void removeTile(TileEntity tile){
        if (!tile.getWorld().isRemote) {
            removeTile_(getPowerTile(tile.getPos()));
        }
    }

    private void removeTile_(T t){
        remove(t);
        removeFromRegistry(t);
    }

    private void resetPosAndSurroundings(BlockPos pos){
        surroundingChecker.resetPositionData(pos);
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos o = pos.offset(facing);
            if (getPowerTile(o) != null) {
                surroundingChecker.resetPositionData(o);
            }
        }
    }

    protected abstract boolean isValidTile(TileEntity tile);

    protected abstract T generate(TileEntity tile);

    protected abstract void add(T t);

    /*
     * Called when a MultiPart gets added to an already registered tile.
     *
     * @param t The tile-reference object
     * @param multiPart The MultiPart that was added, could be an insignificant one
     */
    protected abstract void onExtraMultiPartAdded(T t, IMultipart multiPart);

    protected void onChunkLoad(Collection<TileEntity> loadingTiles){
        for (TileEntity tile : loadingTiles){
            addTile(tile);
        }
    }

    protected abstract void remove(T t);

    /*
     * Called when a MultiPart gets removed from an tile that is still valid.
     *
     * @param t The tile-reference object
     * @param multiPart The MultiPart that was removed, could be an insignificant one
       */
    protected abstract void onMultiPartRemoved(T t, IMultipart multiPart);

    protected abstract void onChunkUnload(Collection<T> unloadingObjects);

    protected abstract void onTick();

    /**
     * Gets called when the world unloads, just before it is removed from the registry and made ready for the GC
     */
    public abstract void invalidate();

    @Override
    public final void onWorldUnload() {
        MinecraftForge.EVENT_BUS.unregister(this);
        invalidate();
    }

    @Override
    public final void tick() {
        if (!pending.isEmpty()){
            for (T t = pending.poll(); t != null; t = pending.poll()){
                add(t);
            }
        }
        onTick();
    }

    public final T getPowerTile(BlockPos pos){
        return registeredTiles.get(pos);
    }

    public void removeFromRegistry(T t){
        registeredTiles.remove(t.getPos());
        surroundingChecker.removePosition(t.getPos());
    }

    public final void addPendingAdd(T t){
        pending.add(t);
    }

    public static ICapabilityProvider forSlottedProvider(final IMultipartContainer provider){
        return new ICapabilityProvider() {
            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                return MultipartCapabilityHelper.hasCapability(provider, capability, facing);
            }

            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                return MultipartCapabilityHelper.getCapability(provider, capability, facing);
            }
        };
    }

}
