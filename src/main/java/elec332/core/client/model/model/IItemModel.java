package elec332.core.client.model.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.List;

/**
 * Created by Elec332 on 21-11-2015.
 */
public interface IItemModel extends ISmartItemModel {

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing facing);

    @Override
    public List<BakedQuad> getGeneralQuads();

}
