package elec332.core.abstraction.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

/**
 * Created by Elec332 on 25-3-2018.
 */
public interface IBlockRotationHandler {

    default public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state;
    }

    default public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state;
    }

}
