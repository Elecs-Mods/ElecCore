package elec332.core.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Elec332 on 29-12-2018
 */
public interface IAbstractBlock {

    default public void addBoxes(IBlockState state, World world, BlockPos pos, List<AxisAlignedBB> boxes) {
        boxes.add(state.getBoundingBox(world, pos));
    }

    default public void addSelectionBoxes(IBlockState state, World world, BlockPos pos, List<AxisAlignedBB> boxes) {
        addBoxes(state, world, pos, boxes);
    }

    default public void addCollisionBoxes(IBlockState state, World world, BlockPos pos, List<AxisAlignedBB> boxes) {
        addBoxes(state, world, pos, boxes);
    }

    default public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, RayTraceResult hit) {
        return false;
    }

    default public boolean canBreak(World world, BlockPos pos, EntityPlayer player) {
        return true;
    }

}
