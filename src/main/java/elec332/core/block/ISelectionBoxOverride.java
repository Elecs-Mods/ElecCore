package elec332.core.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IWorld;

/**
 * Created by Elec332 on 1-2-2019
 */
public interface ISelectionBoxOverride {

    default public VoxelShape getSelectionBox(BlockState state, IWorld world, BlockPos pos, PlayerEntity player, RayTraceResult hit) {
        return state.getShape(world, pos);
    }

}
