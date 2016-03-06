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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayDeque;
import java.util.Queue;

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
        if (!world.isRemote) {
            IMultipart multiPart = event.part;
            if (WorldHelper.getDimID(multiPart.getWorld()) != WorldHelper.getDimID(world)) {
                return;
            }
            BlockPos pos = multiPart.getPos();
            if (!WorldHelper.chunkLoaded(world, pos)) {
                throw new RuntimeException();
            }
            T t = getPowerTile(pos);
            TileEntity tile = WorldHelper.getTileAt(world, pos);
            if (t == null && isValidTile(tile)) {
                addTile(tile);
            }
        }
    }

    @SubscribeEvent
    public final void partRemove(PartEvent.Remove event){
        if (!world.isRemote) {
            IMultipart multiPart = event.part;
            if (WorldHelper.getDimID(multiPart.getWorld()) != WorldHelper.getDimID(world)) {
                return;
            }
            BlockPos pos = multiPart.getPos();
            T t = getPowerTile(pos);
            if (t != null){
                TileEntity tile = t.getTile();
                if(!isValidTile(tile)) {
                    removeTile(tile);
                }
            }
        }
    }

    public final void addTile(TileEntity tile){
        if (!world.isRemote) {
            BlockPos tilePos = tile.getPos();
            if (WorldHelper.getDimID(world) != WorldHelper.getDimID(this.world) || tile instanceof IMultipartContainer) {
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
            addT(powerTile);
        }
    }

    public final void removeTile(TileEntity tile){
        if (!world.isRemote) {
            removeTile_(getPowerTile(tile.getPos()));
        }
    }

    private void removeTile_(T t){
        remove(t);
        removeFromRegistry(t);
    }

    private void addT(T t){
        BlockPos pos = t.getPos();
        surroundingChecker.resetPositionData(pos);
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos o = pos.offset(facing);
            if (getPowerTile(o) != null) {
                surroundingChecker.resetPositionData(o);
            }
        }
        add(t);
    }

    protected abstract boolean isValidTile(TileEntity tile);

    protected abstract T generate(TileEntity tile);

    protected abstract void add(T t);

    protected abstract void remove(T t);

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
                addT(t);
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
