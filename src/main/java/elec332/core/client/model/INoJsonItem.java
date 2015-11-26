package elec332.core.client.model;

import elec332.core.client.model.model.IModelAndTextureLoader;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 19-11-2015.
 */
public interface INoJsonItem extends IModelAndTextureLoader {

    @SideOnly(Side.CLIENT)
    public ISmartItemModel getItemModel(Item item, int meta);

}
