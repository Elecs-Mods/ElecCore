package elec332.core.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Elec332 on 29-12-2018
 */
public interface IAbstractBlock {

    default public void addSelectionBoxes(BlockState state, World world, BlockPos pos, List<AxisAlignedBB> boxes) {
        boxes.add(state.getShape(world, pos).getBoundingBox());
    }

    default public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, RayTraceResult hit) {
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

}
