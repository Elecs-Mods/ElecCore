package elec332.core.tile.sub;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 20-2-2018
 */
public interface ISubTileLogic extends ICapabilityProvider {

    public void readFromNBT(CompoundTag compound);

    @Nonnull
    public CompoundTag writeToNBT(@Nonnull CompoundTag compound);

    @Nullable
    public Level getWorld();

    public BlockPos getPos();

    public void markDirty();

    public boolean hasWorld();

    public default void onRemoved() {
    }

    public default void neighborChanged(BlockPos neighborPos, Block changedBlock, boolean observer) {
    }

    public default boolean removedByPlayer(@Nonnull Player player, boolean willHarvest, @Nonnull BlockHitResult hit) {
        return false;
    }

    public default boolean canBeRemoved() {
        return true;
    }

    public default boolean onBlockActivated(Player player, Hand hand, BlockHitResult hit) {
        return false;
    }

    public default VoxelShape getShape(BlockState state, int data) {
        return VoxelShapes.fullCube();
    }

    default public VoxelShape getSelectionBox(BlockState state, @Nonnull BlockHitResult hit, Player player) {
        return getShape(state, hit.subHit);
    }

    @Nullable
    public default ItemStack getStack(@Nonnull BlockHitResult hit, Player player) {
        return null;
    }

    public default void invalidate() {
    }

    public default void onLoad() {
    }

    public default void sendInitialLoadPackets() {
    }

    public void sendPacket(int ID, CompoundTag data);

    public default void onDataPacket(int id, CompoundTag tag) {
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side);

}
