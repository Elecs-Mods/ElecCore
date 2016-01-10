package elec332.core.client.model.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Elec332 on 15-11-2015.
 */
@SideOnly(Side.CLIENT)
public interface IBlockModel extends ISmartBlockModel {

    @Override
    public ISmartBlockModel handleBlockState(IBlockState state);

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing facing);

    @Override
    public List<BakedQuad> getGeneralQuads();

}
