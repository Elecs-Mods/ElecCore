package elec332.core.api.info;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoDataAccessorBlock extends IInfoDataAccessor {

    @Nonnull
    @Override
    public PlayerEntity getPlayer();

    @Nonnull
    @Override
    public World getWorld();

    @Nonnull
    public BlockPos getPos();

    @Nonnull
    @Override
    public CompoundTag getData();

    @Nonnull
    @Override
    public Vec3d getHitVec();

    @Nonnull
    public Direction getSide();

    @Nonnull
    public BlockState getBlockState();

    @Nonnull
    public Block getBlock();

    @Nullable
    public TileEntity getTileEntity();

    @Nullable
    public ItemStack getStack();

    @Nonnull
    public BlockRayTraceResult getRayTraceResult();

}
