package elec332.core.client.model.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Elec332 on 21-11-2015.
 */
@SideOnly(Side.CLIENT)
public interface IItemModel extends ISmartItemModel {

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing facing);

    @Override
    public List<BakedQuad> getGeneralQuads();

}
