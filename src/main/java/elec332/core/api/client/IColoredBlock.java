package elec332.core.api.client;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 16-9-2016.
 */
public interface IColoredBlock {

    default int colorMultiplier(@Nonnull BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
        return -1;
    }

}
