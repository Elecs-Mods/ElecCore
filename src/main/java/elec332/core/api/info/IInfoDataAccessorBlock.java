package elec332.core.api.info;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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
    public CompoundNBT getData();

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
