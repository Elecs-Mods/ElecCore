package elec332.core.client.util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 12-12-2015.
 */
@SideOnly(Side.CLIENT)
public class GuiDraw {

    private static final GuiDrawGui gui;

    public static void drawRect(int left, int top, int right, int bottom, int color){
        Gui.drawRect(left, top, right, bottom, color);
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight){
        Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight){
        Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height){
        gui.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV){
        gui.drawTexturedModalRect(xCoord, yCoord, minU, minV, maxU, maxV);
    }

    public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn){
        gui.drawTexturedModalRect(xCoord, yCoord, textureSprite, widthIn, heightIn);
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        gui.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    public static Gui getGui() {
        return gui;
    }

    static {
        gui = new GuiDrawGui();
    }

    private static class GuiDrawGui extends Gui {

        private GuiDrawGui(){
            zLevel = 300.0F;
        }

        @Override
        public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
            super.drawGradientRect(left, top, right, bottom, startColor, endColor);
        }

    }
}
