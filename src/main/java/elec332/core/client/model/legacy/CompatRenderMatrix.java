package elec332.core.client.model.legacy;

import com.mojang.blaze3d.platform.GlStateManager;
import elec332.core.api.client.IRenderMatrix;
import elec332.core.api.client.ITextureLocation;
import elec332.core.client.RenderHelper;

/**
 * Created by Elec332 on 6-7-2020
 */
public class CompatRenderMatrix implements IRenderMatrix {

    @Override
    public void translate(double x, double y, double z) {
        GlStateManager.translated(x, y, z);
    }

    @Override
    public void scale(float scaleX, float scaleY, float scaleZ) {
        GlStateManager.scalef(scaleX, scaleY, scaleZ);
    }

    @Override
    public void rotateDegrees(float angle, float x, float y, float z) {
        GlStateManager.rotatef(angle, x, y, z);
    }

    @Override
    public void bindTexture(ITextureLocation location) {
        RenderHelper.bindTexture(location.getTextureLocation());
    }

    @Override
    public void push() {
        GlStateManager.pushMatrix();
    }

    @Override
    public void pop() {
        GlStateManager.popMatrix();
    }

}
