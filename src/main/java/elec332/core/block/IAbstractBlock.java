package elec332.core.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 29-12-2018
 */
public interface IAbstractBlock {

    default public void addSelectionBoxes(BlockState state, World world, BlockPos pos, List<AxisAlignedBB> boxes) {
        boxes.add(state.getShape(world, pos).getBoundingBox());
    }

    default public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return false;
    }

    default public boolean canBreak(World world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    @SuppressWarnings("unchecked")
    default public <T extends TileEntity> T getTileEntity(IBlockReader world, BlockPos pos) {
        return (T) world.getTileEntity(pos);
    }

    default public <T extends TileEntity> T getTileEntity(IBlockReader world, BlockPos pos, Class<T> type) {
        return getTileEntity(world, pos);
    }

    default public List<ItemStack> getDrops(List<ItemStack> drops, @Nonnull LootContext.Builder builder, Entity entity, World world, BlockPos pos, @Nonnull BlockState state, ItemStack stack) {
        return drops;
    }

}
