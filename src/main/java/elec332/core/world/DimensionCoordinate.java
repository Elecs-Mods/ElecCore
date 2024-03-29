package elec332.core.world;

import com.google.common.base.Preconditions;
import elec332.core.ElecCore;
import elec332.core.util.FMLHelper;
import elec332.core.util.NBTBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

/**
 * Created by Elec332 on 22-7-2016.
 * <p>
 * A position which includes the dimension it is located in
 */
public final class DimensionCoordinate implements INBTSerializable<CompoundNBT> {

    public DimensionCoordinate(DimensionCoordinate dim) {
        this(dim.dim, dim.pos, dim.worldRef);
    }

    public DimensionCoordinate(IWorld world, BlockPos pos) {
        this(WorldHelper.getDimID(world), pos, new WeakReference<>(world));
    }

    public DimensionCoordinate(RegistryKey<World> dimension, BlockPos pos) {
        this(dimension, pos, null);
    }

    private DimensionCoordinate(RegistryKey<World> dimension, BlockPos pos, WeakReference<IWorld> worldRef) {
        this.pos = Validate.notNull(pos, "Cannot have a DimensionCoordinate with a null BlockPos!");
        this.dim = dimension;
        this.worldRef = worldRef;
    }

    private final BlockPos pos;
    private final RegistryKey<World> dim;
    private WeakReference<IWorld> worldRef;

    @Nonnull
    public BlockPos getPos() {
        return pos;
    }

    public RegistryKey<World> getDimension() {
        return dim;
    }

    @Nullable
    public IWorld getWorld() {
        if (FMLHelper.getLogicalSide() == LogicalSide.CLIENT) {
            IWorld world = ElecCore.proxy.getClientWorld();
            if (WorldHelper.getDimID(world) == dim) {
                return world;
            }
            return null;
        } else {
            if (worldRef == null || worldRef.get() == null) {
                worldRef = new WeakReference<>(WorldHelper.getServerWorld(dim));
            }
            return worldRef.get();
        }
    }

    @Nullable
    public TileEntity getTileEntity() {
        return getTileEntity(getWorld());
    }

    @Nullable
    public TileEntity getTileEntity(IWorld world) {
        if (loaded(world)) {
            return WorldHelper.getTileAt(world, pos);
        }
        return null;
    }

    @Nullable
    public BlockState getBlockState() {
        return getBlockState(getWorld());
    }

    @Nullable
    public BlockState getBlockState(IWorld world) {
        if (loaded(world)) {
            return WorldHelper.getBlockState(world, pos);
        }
        return null;
    }

    public boolean isLoaded() {
        return loaded(getWorld());
    }

    private boolean loaded(IWorld world) {
        return WorldHelper.chunkLoaded(world, pos);
    }

    @Override
    public CompoundNBT serializeNBT() {
        return new NBTBuilder().setBlockPos(pos).setResourceLocation("dim", Preconditions.checkNotNull(dim.func_240901_a_())).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        throw new UnsupportedOperationException();
    }

    public static DimensionCoordinate fromNBT(CompoundNBT tag) {
        NBTBuilder nbt = new NBTBuilder(tag);
        return new DimensionCoordinate(WorldHelper.getWorldKey(nbt.getResourceLocation("dim")), nbt.getBlockPos());
    }

    public static DimensionCoordinate fromTileEntity(TileEntity tile) {
        return new DimensionCoordinate(tile.getWorld(), tile.getPos());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DimensionCoordinate && ((DimensionCoordinate) obj).pos.equals(pos) && ((DimensionCoordinate) obj).dim == dim;
    }

    @Override
    public int hashCode() {
        return 31 * pos.hashCode() + dim.hashCode() * (dim.hashCode() + 1);
    }

    @Override
    public String toString() {
        return "[DimensionCoordinate: " + pos.toString() + " dim: " + dim + "]";
    }

}
