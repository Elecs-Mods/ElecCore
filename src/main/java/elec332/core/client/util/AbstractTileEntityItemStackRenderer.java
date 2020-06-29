package elec332.core.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 23-12-2019
 */
public abstract class AbstractTileEntityItemStackRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void render(ItemStack stack, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        renderItem(stack, matrixStack, renderTypeBuffer, combinedLightIn, combinedOverlayIn);
    }

    protected abstract void renderItem(ItemStack stack, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderTypeBuffer, int combinedLightIn, int combinedOverlayIn);

}
