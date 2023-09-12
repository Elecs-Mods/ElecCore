package elec332.core.world;

import com.google.common.base.Preconditions;
import elec332.core.ElecCore;
import elec332.core.util.FMLHelper;
import elec332.core.util.NBTBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
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
public final class DimensionCoordinate implements INBTSerializable<CompoundTag> {

    public DimensionCoordinate(DimensionCoordinate dim) {
        this(dim.dim, dim.pos, dim.worldRef);
    }

    public DimensionCoordinate(Level world, BlockPos pos) {
        this(WorldHelper.getDimID(world), pos, new WeakReference<>(world));
    }

    public DimensionCoordinate(ResourceKey<Level> dimension, BlockPos pos) {
        this(dimension, pos, null);
    }

    private DimensionCoordinate(ResourceKey<Level> dimension, BlockPos pos, WeakReference<Level> worldRef) {
        this.pos = Validate.notNull(pos, "Cannot have a DimensionCoordinate with a null BlockPos!");
        this.dim = dimension;
        this.worldRef = worldRef;
    }

    private final BlockPos pos;
    private final ResourceKey<Level> dim;
    private WeakReference<Level> worldRef;

    @Nonnull
    public BlockPos getPos() {
        return pos;
    }

    public ResourceKey<Level> getDimension() {
        return dim;
    }

    @Nullable
    @SuppressWarnings("all")
    public Level getWorld() {
        if (FMLHelper.getLogicalSide() == LogicalSide.CLIENT) {
            Level world = ElecCore.proxy.getClientWorld();
            if (WorldHelper.getDimID(world) == dim) {
                return world;
            }
            return null;
        } else {
            if (worldRef == null || worldRef.get() == null) {
                worldRef = new WeakReference<>(WorldHelper.getServerWorldDirect(dim));
            }
            return worldRef.get();
        }
    }

    @Nullable
    public BlockEntity getBlockEntity() {
        return getBlockEntity(getWorld());
    }

    @Nullable
    public BlockEntity getBlockEntity(Level world) {
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
    public BlockState getBlockState(Level world) {
        if (loaded(world)) {
            return WorldHelper.getBlockState(world, pos);
        }
        return null;
    }

    public boolean isLoaded() {
        return loaded(getWorld());
    }

    private boolean loaded(Level world) {
        return WorldHelper.chunkLoaded(world, pos);
    }

    @Override
    public CompoundTag serializeNBT() {
        return new NBTBuilder().setBlockPos(pos).setResourceLocation("dim", Preconditions.checkNotNull(dim.location())).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        throw new UnsupportedOperationException();
    }

    public static DimensionCoordinate fromNBT(CompoundTag tag) {
        NBTBuilder nbt = new NBTBuilder(tag);
        return new DimensionCoordinate(ResourceKey.create(Registry.DIMENSION_REGISTRY, nbt.getResourceLocation("dim")), nbt.getBlockPos());
    }

    public static DimensionCoordinate fromBlockEntity(BlockEntity tile) {
        return new DimensionCoordinate(tile.getLevel(), tile.getBlockPos());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DimensionCoordinate && ((DimensionCoordinate) obj).pos.equals(pos) && ((DimensionCoordinate) obj).dim == dim;
    }

    @Override
    public int hashCode() {
        return 31 * pos.hashCode() + dim.hashCode() * (dim.toString().hashCode() + 1);
    }

    @Override
    public String toString() {
        return "[DimensionCoordinate: " + pos.toString() + " dim: " + dim + "]";
    }

}
