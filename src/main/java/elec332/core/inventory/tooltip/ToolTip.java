package elec332.core.inventory.tooltip;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.client.render.InventoryRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class ToolTip{

    public ToolTip(){
        this((ColouredString) null);
    }

    public ToolTip(ColouredString colouredString){
        this(Lists.newArrayList(colouredString));
    }

    public ToolTip(List<ColouredString> s){
        this.tooltip = s;
    }

    private final List<ColouredString> tooltip;

    @SideOnly(Side.CLIENT)
    public void renderTooltip(int mouseX, int mouseY, int guiLeft, int guiTop){
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        if (!tooltip.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            for (ColouredString colouredString : tooltip){
                int l = fontRenderer.getStringWidth(colouredString.toString());
                if (l > k)
                    k = l;
            }
            int j2 = mouseX + 12 - guiLeft;
            int k2 = mouseY - 12 - guiTop;
            int i1 = 8;
            if (tooltip.size() > 1) {
                i1 += 2 + (tooltip.size() - 1) * 10;
            }

            if (j2 + k > guiLeft)
            {
                j2 -= 28 + k;
            }

            if (k2 + i1 + 6 > guiTop)
            {
                k2 = guiTop - i1 - 6;
            }

            float zLevel = 300.0F;
            int j1 = -267386864;
            InventoryRenderHelper.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1, zLevel);
            InventoryRenderHelper.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1, zLevel);
            InventoryRenderHelper.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1, zLevel);
            InventoryRenderHelper.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1, zLevel);
            InventoryRenderHelper.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1, zLevel);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            InventoryRenderHelper.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1, zLevel);
            InventoryRenderHelper.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1, zLevel);
            InventoryRenderHelper.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1, zLevel);
            InventoryRenderHelper.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1, zLevel);
            for (int i2 = 0; i2 < tooltip.size(); ++i2) {
                String s1 = tooltip.get(i2).toString();
                fontRenderer.drawStringWithShadow(s1, j2, k2, -1);
                if (i2 == 0) {
                    k2 += 2;
                }
                k2 += 10;
            }
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    public static class ColouredString{
        public ColouredString(String s){
            this(EnumChatFormatting.GRAY, s);
        }

        public ColouredString(EnumChatFormatting colour, String s){
            this.string = colour+s;
        }

        private final String string;

        @Override
        public String toString() {
            return this.string;
        }
    }
}
