package elec332.core.client.util;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;

/**
 * Created by Elec332 on 23-12-2019
 */
public abstract class AbstractTileEntityItemStackRenderer extends BlockEntityWithoutLevelRenderer {

    public AbstractTileEntityItemStackRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
    }

    @Override
    public final void renderByItem(ItemStack itemStackIn) {
        renderItem(itemStackIn);
    }

    protected abstract void renderItem(ItemStack stack);

}
