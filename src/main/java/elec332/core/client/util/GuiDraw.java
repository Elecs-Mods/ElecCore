package elec332.core.client.util;

import elec332.core.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Created by Elec332 on 12-12-2015.
 */
@OnlyIn(Dist.CLIENT)
public class GuiDraw {

    private static final GuiDrawGui gui;
    public static final Minecraft mc;
    private static final int zLevel = 0;

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        Screen.fill(left, top, right, bottom, color);
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        Screen.blit(x, y, u, v, width, height, textureWidth, textureHeight);
        //Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, int u, int v, int uWidth, int vHeight, int width, int height, int tileWidth, int tileHeight) {
        Screen.blit(x, y, width, height, u, v, uWidth, vHeight, tileWidth, tileHeight);
        //Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        gui.blit(x, y, textureX, textureY, width, height);
        //gui.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    //public static void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
    //gui.blit(xCoord, yCoord, minU, minV, maxU, maxV);
    //gui.drawTexturedModalRect(xCoord, yCoord, minU, minV, maxU, maxV);
    //}

    public static void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        Screen.blit(xCoord, yCoord, zLevel, widthIn, heightIn, textureSprite);
        //gui.drawTexturedModalRect(xCoord, yCoord, textureSprite, widthIn, heightIn);
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        gui.fillGradient(left, top, right, bottom, startColor, endColor);
    }

    public static void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        gui.drawCenteredString(fontRenderer, text, x, y, color);
    }

    public static void drawString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        gui.drawString(fontRenderer, text, x, y, color);
    }

    public static void drawHoveringText(List<String> textLines, int x, int y) {
        gui.renderTooltip(textLines, x, y, RenderHelper.getMCFontrenderer());
    }

    public static void drawHoveringText(List<String> textLines, int x, int y, @Nullable FontRenderer fontrenderer) {
        gui.renderTooltip(textLines, x, y, fontrenderer == null ? RenderHelper.getMCFontrenderer() : fontrenderer);
    }

    public static void drawHoveringText(String text, int x, int y) {
        gui.renderTooltip(Collections.singletonList(text), x, y, gui.getFont());
    }

    public static List<String> getItemToolTip(ItemStack stack) {
        return gui.getTooltipFromItem(stack);
    }

    public static void drawDefaultBackground() {
        if (mc.currentScreen != null) {
            mc.currentScreen.renderBackground();
        } else {
            gui.width = mc.mainWindow.getScaledWidth();
            gui.height = mc.mainWindow.getScaledHeight();
            gui.renderBackground();
        }
    }

    public static void renderToolTip(ItemStack stack, int x, int y) {
        gui.renderTooltip(getItemToolTip(stack), x, y);
    }

    public static Screen getGui() {
        return gui;
    }

    static {
        gui = new GuiDrawGui();
        mc = Minecraft.getInstance();
    }

    private static class GuiDrawGui extends Screen {

        private GuiDrawGui() {
            super(null);
            //zLevel = 0.0F;
            minecraft = Minecraft.getInstance();
        }

        private FontRenderer getFont() {
            return font;
        }

        @Override
        public void fillGradient(int p_fillGradient_1_, int p_fillGradient_2_, int p_fillGradient_3_, int p_fillGradient_4_, int p_fillGradient_5_, int p_fillGradient_6_) {
            super.fillGradient(p_fillGradient_1_, p_fillGradient_2_, p_fillGradient_3_, p_fillGradient_4_, p_fillGradient_5_, p_fillGradient_6_);
        }

    }

}
