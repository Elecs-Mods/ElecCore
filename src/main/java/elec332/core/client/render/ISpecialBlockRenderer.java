package elec332.core.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 18-11-2015.
 */
@SideOnly(Side.CLIENT)
public interface ISpecialBlockRenderer {

    public boolean renderBlock(IBlockAccess iba, IBlockState state, BlockPos blockPosIn, VertexBuffer renderer);

}
