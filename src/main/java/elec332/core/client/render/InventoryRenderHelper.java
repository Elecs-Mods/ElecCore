package elec332.core.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class InventoryRenderHelper {

    @Deprecated
    public static void bindTexture(ResourceLocation rl){
        Minecraft.getMinecraft().renderEngine.bindTexture(rl);
    }

    public static void drawGradientRect(int i1, int i2, int i3, int i4, int i5, int i6, float zLevel) {
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
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double)i3, (double)i2, (double)zLevel);
        tessellator.addVertex((double)i1, (double)i2, (double)zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double)i1, (double)i4, (double)zLevel);
        tessellator.addVertex((double)i3, (double)i4, (double)zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
