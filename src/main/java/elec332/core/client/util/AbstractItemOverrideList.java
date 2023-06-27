package elec332.core.client.util;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 19-1-2020
 */
public abstract class AbstractItemOverrideList extends ItemOverrideList {

    @Nullable
    @Override
    public IBakedModel func_239290_a_(@Nonnull IBakedModel model, @Nonnull ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
        return getModel(model, stack, worldIn, entityIn);
    }

    @Nullable
    protected abstract IBakedModel getModel(@Nonnull IBakedModel model, @Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity);

}
