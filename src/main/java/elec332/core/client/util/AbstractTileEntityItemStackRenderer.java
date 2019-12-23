package elec332.core.client.util;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 23-12-2019
 */
public abstract class AbstractTileEntityItemStackRenderer extends ItemStackTileEntityRenderer {

    @Override
    public final void renderByItem(ItemStack itemStackIn) {
        renderItem(itemStackIn);
    }

    protected abstract void renderItem(ItemStack stack);

}
