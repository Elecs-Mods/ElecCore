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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoDataAccessorBlock extends IInfoDataAccessor {

    @Nonnull
    @Override
    PlayerEntity getPlayer();

    @Nonnull
    @Override
    World getWorld();

    @Nonnull
    BlockPos getPos();

    @Nonnull
    @Override
    CompoundNBT getData();

    @Nonnull
    @Override
    Vector3d getHitVec();

    @Nonnull
    Direction getSide();

    @Nonnull
    BlockState getBlockState();

    @Nonnull
    Block getBlock();

    @Nullable
    TileEntity getTileEntity();

    @Nullable
    ItemStack getStack();

    @Nonnull
    BlockRayTraceResult getRayTraceResult();

}
