package elec332.core.world;

import elec332.core.ElecCore;
import elec332.core.util.FMLHelper;
import elec332.core.util.NBTBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.DimensionManager;
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
public final class DimensionCoordinate implements INBTSerializable<NBTTagCompound> {

    public DimensionCoordinate(DimensionCoordinate dim) {
        this(dim.dim, dim.pos, dim.worldRef);
    }

    public DimensionCoordinate(IWorld world, BlockPos pos) {
        this(WorldHelper.getDimID(world), pos, new WeakReference<>(world));
    }

    public DimensionCoordinate(int dimension, BlockPos pos) {
        this(dimension, pos, null);
    }

    private DimensionCoordinate(int dimension, BlockPos pos, WeakReference<IWorld> worldRef) {
        this.pos = Validate.notNull(pos, "Cannot have a DimensionCoordinate with a null BlockPos!");
        this.dim = dimension;
        this.worldRef = worldRef;
    }

    private final BlockPos pos;
    private final int dim;
    private WeakReference<IWorld> worldRef;

    @Nonnull
    public BlockPos getPos() {
        return pos;
    }

    public int getDimension() {
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
                worldRef = new WeakReference<>(DimensionManager.getWorld(dim));
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
    public IBlockState getBlockState() {
        return getBlockState(getWorld());
    }

    @Nullable
    public IBlockState getBlockState(IWorld world) {
        if (loaded(world)) {
            return WorldHelper.getBlockState(world, pos);
        }
        return null;
    }

    public boolean isLoaded() {
        return loaded(getWorld());
    }

    private boolean loaded(IWorld world) {
        return world != null && WorldHelper.chunkLoaded(world, pos);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return new NBTBuilder().setBlockPos(pos).setInteger("dim", dim).serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        throw new UnsupportedOperationException();
    }

    public static DimensionCoordinate fromNBT(NBTTagCompound tag) {
        NBTBuilder nbt = new NBTBuilder(tag);
        return new DimensionCoordinate(nbt.getInteger("dim"), nbt.getBlockPos());
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
        return 31 * pos.hashCode() + dim;
    }

    @Override
    public String toString() {
        return "[DimensionCoordinate: " + pos.toString() + " dim: " + dim + "]";
    }

}
