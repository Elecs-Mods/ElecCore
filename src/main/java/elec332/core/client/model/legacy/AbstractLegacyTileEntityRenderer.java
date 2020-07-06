package elec332.core.client.model.legacy;

import com.mojang.blaze3d.matrix.MatrixStack;
import elec332.core.api.client.IRenderMatrix;
import elec332.core.client.util.AbstractTileEntityRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 6-7-2020
 */
public abstract class AbstractLegacyTileEntityRenderer<T extends TileEntity> extends AbstractTileEntityRenderer<T> {

    @Override
    public void render(@Nonnull T tileEntityIn, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        render(tileEntityIn, partialTicks, CompatRenderMatrix.wrap(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn));
    }

    public abstract void render(T tile, float partialTicks, IRenderMatrix renderMatrix);

}
