package elec332.core.inventory.window;

import elec332.core.main.ElecCore;
import elec332.core.util.InventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

/**
 * Created by Elec332 on 28-11-2016.
 */
public final class WindowGui extends GuiContainer {

    public WindowGui(EntityPlayer player, Window window) {
        this(new WindowContainer(player, window));
    }

    public WindowGui(WindowContainer container) {
        super(container);
        container.windowContainerHandler.windowGui = this;
        this.window = container.getWindow();
        this.xSize = window.xSize;
        this.ySize = window.ySize;
    }

    private final Window window;

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        window.width = width;
        window.height = height;
        window.guiLeft = (width - this.xSize) / 2;
        window.guiTop = (height - this.ySize) / 2;
        super.setWorldAndResolution(mc, width, height);
        window.initWindow_();
    }

    @Override
    public void setGuiSize(int w, int h) {
        super.setGuiSize(w, h);
        window.width = w;
        window.height = h;
        window.guiLeft = (this.width - this.xSize) / 2;
        window.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return window.doesWindowPauseGame();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!window.mouseClicked(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!window.keyTyped(typedChar, keyCode)){
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        window.handleMouseInput();
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, @Nonnull ClickType type) {
        window.handleMouseClick(slotIn == null ? null : ((WindowContainer.WidgetLinkedSlot)slotIn).widget, slotId, mouseButton, type);
    }

    void handleMouseClickDefault(Slot slotIn, int slotId, int mouseButton, @Nonnull ClickType type){
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        window.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        window.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GlStateManager.popMatrix();
    }

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        List<String> list = InventoryHelper.getTooltip(stack, ElecCore.proxy.getClientPlayer(), this.mc.gameSettings.advancedItemTooltips);
        window.modifyTooltip(list, ((WindowContainer.WidgetLinkedSlot) hoveredSlot).widget, stack, x, y);
        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            } else {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }

        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        this.drawHoveringText(list, x, y, (font == null ? fontRenderer : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        window.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.popMatrix();
    }

}
