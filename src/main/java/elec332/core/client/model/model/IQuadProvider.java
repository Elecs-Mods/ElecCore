package elec332.core.client.model.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 11-3-2016.
 */
public interface IQuadProvider {

    @SideOnly(Side.CLIENT)
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, EnumFacing side, long random);

}
