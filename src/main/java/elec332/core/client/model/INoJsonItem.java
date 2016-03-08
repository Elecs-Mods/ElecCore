package elec332.core.client.model;

import elec332.core.client.model.model.IItemModel;
import elec332.core.client.model.model.IModelAndTextureLoader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 19-11-2015.
 */
public interface INoJsonItem extends IModelAndTextureLoader {

    @SideOnly(Side.CLIENT)
    public IItemModel getItemModel(ItemStack stack, World world, EntityLivingBase entity);

}
