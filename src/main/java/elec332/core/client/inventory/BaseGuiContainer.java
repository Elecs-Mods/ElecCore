package elec332.core.client.inventory;

import elec332.core.inventory.BaseContainer;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.widget.IWidget;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

/**
 * Created by Elec332 on 4-4-2015.
 */
@SideOnly(Side.CLIENT)
public abstract class BaseGuiContainer extends GuiContainer implements IResourceLocationProvider {

     BaseGuiContainer(BaseContainer container) {
        super(container);
        this.container = container;
    }

    protected BaseContainer container;

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        for (IWidget widget : container.getWidgets()){
            if (!widget.isHidden() && widget.isMouseOver(translatedMouseX(mouseX), translatedMouseY(mouseY)) && widget.mouseClicked(translatedMouseX(mouseX), translatedMouseY(mouseY), button))
                return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) this.guiLeft, (float) this.guiTop, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        for (IWidget widget : container.getWidgets()){
            if (widget.isHidden()) {
                continue;
            }
            if (widget.isMouseOver(translatedMouseX(mouseX), translatedMouseY(mouseY))) {
                ToolTip toolTip = widget.getToolTip(translatedMouseX(mouseX), translatedMouseY(mouseY));
                if (toolTip != null) {
                    toolTip.renderTooltip(mouseX, mouseY, this.guiLeft, this.guiTop);
                }
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(getBackgroundImageLocation());
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        for (IWidget widget : container.getWidgets()){
            if (!widget.isHidden())
                widget.draw(null, k, l, translatedMouseX(mouseX), translatedMouseY(mouseY));
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private int translatedMouseX(int mouseX){
        return mouseX - this.guiLeft;
    }

    private int translatedMouseY(int mouseY){
        return mouseY - this.guiTop;
    }

}
