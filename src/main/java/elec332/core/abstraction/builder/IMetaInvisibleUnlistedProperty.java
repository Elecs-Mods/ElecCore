package elec332.core.abstraction.builder;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by Elec332 on 25-3-2018.
 */
public interface IMetaInvisibleUnlistedProperty<P extends Comparable<P>> extends IUnlistedProperty<P> {

    default public P getActualProperty(IExtendedBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(this);
    }

}
