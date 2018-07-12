package elec332.core.abstraction.builder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 * Created by Elec332 on 23-3-2018.
 */
public interface IBlockStateSerializer {

    public int getMeta(IBlockState state);

    public IBlockState getState(int meta, Block block);

}
