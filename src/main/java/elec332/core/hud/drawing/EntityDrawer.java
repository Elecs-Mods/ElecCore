package elec332.core.hud.drawing;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import elec332.core.hud.position.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 13-1-2017.
 */
public class EntityDrawer implements IDrawer<Entity> {

    public static final IDrawer<Entity> INSTANCE = new EntityDrawer(0, 0, 30);

    public EntityDrawer(float xOffset, float yOffset, float scale) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.scale = scale;
    }

    private final float xOffset, yOffset, scale;

    @Override
    @OnlyIn(Dist.CLIENT)
    public int draw(Entity drawable, Minecraft mc, Alignment alignment, int x, int y, Object... data) {
        x += xOffset;
        y += yOffset;
        RenderSystem.enableColorMaterial();
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) x, (float) y, 50.0F);

        float scale = this.scale;

        if (data != null) {
            if (data.length > 0) {
                RenderSystem.rotatef((float) data[0], 0, 1, 0);
            }
            if (data.length > 1) {
                scale = scale / (float) data[1];
            }
        }

        RenderSystem.scaled(-scale, scale, scale);
        RenderSystem.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderSystem.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        RenderSystem.translatef(0.0F, 0.0F, 0.0F);
        EntityRendererManager rendermanager = mc.getRenderManager();
        rendermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        rendermanager.renderEntityStatic(drawable, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, new MatrixStack(), irendertypebuffer$impl, 15728880);
        irendertypebuffer$impl.finish();
        rendermanager.setRenderShadow(true);
        RenderSystem.popMatrix();
        return (int) drawable.getWidth();
    }

}
