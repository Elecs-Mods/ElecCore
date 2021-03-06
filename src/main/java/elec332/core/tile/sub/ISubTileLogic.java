package elec332.core.tile.sub;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 20-2-2018
 */
public interface ISubTileLogic extends ICapabilityProvider {

    void readFromNBT(CompoundNBT compound);

    @Nonnull
    CompoundNBT writeToNBT(@Nonnull CompoundNBT compound);

    @Nullable
    World getWorld();

    BlockPos getPos();

    void markDirty();

    boolean hasWorld();

    default void onRemoved() {
    }

    default void neighborChanged(BlockPos neighborPos, Block changedBlock, boolean observer) {
    }

    default boolean removedByPlayer(@Nonnull PlayerEntity player, boolean willHarvest, @Nonnull RayTraceResult hit) {
        return false;
    }

    default boolean canBeRemoved() {
        return true;
    }

    default ActionResultType onBlockActivated(PlayerEntity player, Hand hand, RayTraceResult hit) {
        return ActionResultType.PASS;
    }

    default VoxelShape getShape(BlockState state, int data) {
        return VoxelShapes.fullCube();
    }

    default VoxelShape getSelectionBox(BlockState state, @Nonnull RayTraceResult hit, PlayerEntity player) {
        return getShape(state, hit.subHit);
    }

    @Nullable
    default ItemStack getStack(@Nonnull RayTraceResult hit, PlayerEntity player) {
        return null;
    }

    default void invalidate() {
    }

    default void onLoad() {
    }

    default void sendInitialLoadPackets() {
    }

    void sendPacket(int ID, CompoundNBT data);

    default void onDataPacket(int id, CompoundNBT tag) {
    }

    @Nonnull
    @Override
    <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side);

}
