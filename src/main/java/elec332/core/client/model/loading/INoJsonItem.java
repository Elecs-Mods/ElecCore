package elec332.core.client.model.loading;

import elec332.core.api.client.model.IModelAndTextureLoader;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 19-11-2015.
 */
public interface INoJsonItem extends IModelAndTextureLoader {

    @OnlyIn(Dist.CLIENT)
    public IBakedModel getItemModel(ItemStack stack, World world, EntityLivingBase entity);

}
