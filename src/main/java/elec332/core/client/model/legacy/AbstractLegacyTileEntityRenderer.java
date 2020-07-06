package elec332.core.client.model.legacy;

import elec332.core.api.client.IRenderMatrix;
import elec332.core.client.util.AbstractTileEntityRenderer;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 6-7-2020
 */
public abstract class AbstractLegacyTileEntityRenderer<T extends TileEntity> extends AbstractTileEntityRenderer<T> {

    @Override
    public final void render(@Nonnull T tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        IRenderMatrix renderMatrix = new CompatRenderMatrix();
        renderMatrix.push();
        renderMatrix.translate(x, y, z);
        render(tileEntityIn, partialTicks, renderMatrix);
        renderMatrix.pop();
    }

    public abstract void render(T tile, float partialTicks, IRenderMatrix renderMatrix);

}
