package elec332.core.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Created by Elec332 on 23-1-2016.
 */
public interface IRandomDisplayTickProviderTile {

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, Random random);

}
