package elec332.core.api.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

/**
 * Created by Elec332 on 30-6-2020
 */
public interface IWorldEventHook {

    void markBlockChanged(IWorld world, BlockPos pos, BlockState oldState, BlockState newState);

}
