package elec332.core.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class InventoryRenderHelper {

    @Deprecated
    public static void bindTexture(ResourceLocation rl){
        Minecraft.getMinecraft().renderEngine.bindTexture(rl);
    }

    @Deprecated
    public static void drawGradientRect(int i1, int i2, int i3, int i4, int i5, int i6, float zLevel) {
        throw new IllegalAccessError();}/*
        float f = (float)(i5 >> 24 & 255) / 255.0F;
        float f1 = (float)(i5 >> 16 & 255) / 255.0F;
        float f2 = (float)(i5 >> 8 & 255) / 255.0F;
        float f3 = (float)(i5 & 255) / 255.0F;
        float f4 = (float)(i6 >> 24 & 255) / 255.0F;
        float f5 = (float)(i6 >> 16 & 255) / 255.0F;
        float f6 = (float)(i6 >> 8 & 255) / 255.0F;
        float f7 = (float)(i6 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().setColorRGBA_F(f1, f2, f3, f);
        tessellator.getWorldRenderer().addVertex((double) i3, (double) i2, (double) zLevel);
        tessellator.getWorldRenderer().addVertex((double) i1, (double) i2, (double) zLevel);
        tessellator.getWorldRenderer().setColorRGBA_F(f5, f6, f7, f4);
        tessellator.getWorldRenderer().addVertex((double) i1, (double) i4, (double) zLevel);
        tessellator.getWorldRenderer().addVertex((double)i3, (double)i4, (double)zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }*/

}
