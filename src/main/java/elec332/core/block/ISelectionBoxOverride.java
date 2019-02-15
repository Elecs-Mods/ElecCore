package elec332.core.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;

/**
 * Created by Elec332 on 1-2-2019
 */
public interface ISelectionBoxOverride {

    default public VoxelShape getSelectionBox(IBlockState state, IWorld world, BlockPos pos, EntityPlayer player, RayTraceResult hit) {
        return state.getShape(world, pos);
    }

}
