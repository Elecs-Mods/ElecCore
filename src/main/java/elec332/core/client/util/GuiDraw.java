package elec332.core.client.util;

import elec332.core.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 12-12-2015.
 */
@SideOnly(Side.CLIENT)
public class GuiDraw {

    private static final GuiDrawGui gui;
    public static final Minecraft mc;

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

    public static void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV){
        gui.drawTexturedModalRect(xCoord, yCoord, minU, minV, maxU, maxV);
    }

    public static void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn){
        gui.drawTexturedModalRect(xCoord, yCoord, textureSprite, widthIn, heightIn);
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        gui.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    public static void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        gui.drawCenteredString(fontRenderer, text, x, y, color);
    }

    public static void drawString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        gui.drawString(fontRenderer, text, x, y, color);
    }

    public static void drawHoveringText(List<String> textLines, int x, int y){
        gui.drawHoveringText(textLines, x, y, RenderHelper.getMCFontrenderer());
    }

    public static void drawHoveringText(List<String> textLines, int x, int y, @Nullable FontRenderer fontrenderer){
        gui.drawHoveringText(textLines, x, y, fontrenderer == null ? RenderHelper.getMCFontrenderer() : fontrenderer);
    }

    public static void drawHoveringText(String text, int x, int y) {
        gui.drawHoveringText(text, x, y);
    }

    public static List<String> getItemToolTip(ItemStack stack){
        return gui.getItemToolTip(stack);
    }

    public static void drawDefaultBackground(){
        if (mc.currentScreen != null){
            mc.currentScreen.drawDefaultBackground();
        } else {
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            gui.width = scaledresolution.getScaledWidth();
            gui.height = scaledresolution.getScaledHeight();
            gui.drawDefaultBackground();
        }
    }

    public static void renderToolTip(ItemStack stack, int x, int y){
        gui.renderToolTip(stack, x, y);
    }

    public static GuiScreen getGui() {
        return gui;
    }

    static {
        gui = new GuiDrawGui();
        mc = Minecraft.getMinecraft();
    }

    private static class GuiDrawGui extends GuiScreen {

        private GuiDrawGui(){
            zLevel = 0.0F;
            mc = Minecraft.getMinecraft();
        }

        @Override
        public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
            super.drawGradientRect(left, top, right, bottom, startColor, endColor);
        }

        @Override
        public void drawHoveringText(List<String> textLines, int x, int y, @Nonnull FontRenderer font) {
            super.drawHoveringText(textLines, x, y, font);
        }

        @Override
        public void renderToolTip(ItemStack stack, int x, int y) {
            super.renderToolTip(stack, x, y);
        }

    }

}
