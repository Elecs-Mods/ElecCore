package elec332.core.client.inventory;

import elec332.core.inventory.BaseContainer;
import elec332.core.inventory.widget.Widget;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

/**
 * Created by Elec332 on 4-4-2015.
 */
public abstract class BaseGuiContainer extends GuiContainer implements IResourceLocationProvider{
    public BaseGuiContainer(BaseContainer container) {
        super(container);
        this.container = container;
    }

    protected BaseContainer container;

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        for (Widget widget : container.getWidgets()){
            if (!widget.isHidden() && widget.isMouseOver(translatedMouseX(mouseX), translatedMouseY(mouseY)) && widget.mouseClicked(translatedMouseX(mouseX), translatedMouseY(mouseY), button))
                return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) this.guiLeft, (float) this.guiTop, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        for (Widget widget : container.getWidgets()){
            if (widget.isHidden())
                continue;
            if (widget.getToolTip() != null && widget.isMouseOver(translatedMouseX(mouseX), translatedMouseY(mouseY)))
                widget.getToolTip().renderTooltip(mouseX, mouseY, this.guiLeft, this.guiTop);
        }
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(getBackgroundImageLocation());
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        for (Widget widget : container.getWidgets()){
            if (!widget.isHidden())
                widget.draw(this, k, l, translatedMouseX(mouseX), translatedMouseY(mouseY));
        }
    }

    private int translatedMouseX(int mouseX){
        return mouseX - this.guiLeft;
    }

    private int translatedMouseY(int mouseY){
        return mouseY - this.guiTop;
    }
}
