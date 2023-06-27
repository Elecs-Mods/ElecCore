package elec332.core.client.util;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import elec332.core.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 12-12-2015.
 */
@OnlyIn(Dist.CLIENT)
public class GuiDraw {

    private static final GuiDrawGui gui;
    public static final Minecraft mc;
    private static final int zLevel = 0;

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        Screen.fill(new MatrixStack(), left, top, right, bottom, color);
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        Screen.blit(new MatrixStack(), x, y, u, v, width, height, textureWidth, textureHeight);
        //Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, int u, int v, int uWidth, int vHeight, int width, int height, int tileWidth, int tileHeight) {
        Screen.blit(new MatrixStack(), x, y, width, height, u, v, uWidth, vHeight, tileWidth, tileHeight);
        //Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        gui.blit(new MatrixStack(), x, y, textureX, textureY, width, height);
        //gui.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    //public static void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
    //gui.blit(xCoord, yCoord, minU, minV, maxU, maxV);
    //gui.drawTexturedModalRect(xCoord, yCoord, minU, minV, maxU, maxV);
    //}

    public static void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        Screen.blit(new MatrixStack(), xCoord, yCoord, zLevel, widthIn, heightIn, textureSprite);
        //gui.drawTexturedModalRect(xCoord, yCoord, textureSprite, widthIn, heightIn);
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        gui.fillGradient(new MatrixStack(), left, top, right, bottom, startColor, endColor);
    }

    public static void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        AbstractGui.drawCenteredString(new MatrixStack(), fontRenderer, text, x, y, color);
    }

    public static void drawString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        AbstractGui.drawString(new MatrixStack(), fontRenderer, text, x, y, color);
    }

    public static void drawHoveringText(List<String> textLines, int x, int y) {
        drawHoveringText(textLines, x, y, null);
    }

    public static void drawHoveringText(String text, int x, int y) {
        drawHoveringText(Collections.singletonList(text), x, y, gui.getFont());
    }

    public static void drawHoveringText(List<String> textLines, int x, int y, @Nullable FontRenderer fontrenderer) {
        drawHoveringText(textLines.stream()
                .map(StringTextComponent::new)
                .collect(Collectors.toList()), x, y, fontrenderer);
    }

    public static void drawHoveringText(List<ITextComponent> textLines, int x, int y, @Nullable FontRenderer fontrenderer, Object... fake) {
        drawHoveringText(new MatrixStack(), textLines, x, y, fontrenderer);
    }

    public static void drawHoveringText(MatrixStack matrixStack, List<ITextComponent> textLines, int x, int y, @Nullable FontRenderer fontrenderer) {
        gui.renderHoveringText(new MatrixStack(), Lists.transform(textLines, ITextComponent::func_241878_f), x, y, fontrenderer == null ? RenderHelper.getMCFontrenderer() : fontrenderer);
    }

    public static List<ITextComponent> getItemToolTip(ItemStack stack) {
        return gui.getTooltipFromItem(stack);
    }

    public static void drawDefaultBackground() {
        if (mc.currentScreen != null) {
            mc.currentScreen.renderBackground(new MatrixStack());
        } else {
            gui.width = RenderHelper.getMainWindow().getScaledWidth();
            gui.height = RenderHelper.getMainWindow().getScaledHeight();
            gui.renderBackground(new MatrixStack());
        }
    }

    public static void renderToolTip(ItemStack stack, int x, int y) {
        drawHoveringText(getItemToolTip(stack), x, y, null);
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
        protected void fillGradient(@Nonnull MatrixStack matrixStack, int x1, int y1, int x2, int y2, int p_238468_6_, int p_238468_7_) {
            super.fillGradient(matrixStack, x1, y1, x2, y2, p_238468_6_, p_238468_7_);
        }

        public void renderHoveringText(MatrixStack matrixStack, List<? extends IReorderingProcessor> p_238654_2_, int p_238654_3_, int p_238654_4_, FontRenderer font) {
            super.renderToolTip(matrixStack, p_238654_2_, p_238654_3_, p_238654_4_, font);
        }

    }

}
