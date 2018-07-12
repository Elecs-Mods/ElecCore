package elec332.core.abstraction.builder;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by Elec332 on 25-3-2018.
 */
public interface IMetaInvisibleProperty<P extends Comparable<P>> extends IProperty<P> {

    default public P getActualProperty(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(this);
    }

}
