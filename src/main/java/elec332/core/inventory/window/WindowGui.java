package elec332.core.inventory.window;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import elec332.core.ElecCore;
import elec332.core.client.RenderHelper;
import elec332.core.util.InventoryHelper;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 28-11-2016.
 */
public final class WindowGui extends ContainerScreen<WindowContainer> {

    public WindowGui(int windowId, PlayerEntity player, Window window) {
        this(new WindowContainer(player, window, windowId));
    }

    public WindowGui(WindowContainer container) {
        super(container, container.getPlayer().inventory, new StringTextComponent("window"));
        container.windowContainerHandler.windowGui = this;
        this.window = container.getWindow();
        this.xSize = window.xSize;
        this.ySize = window.ySize;
    }

    private final Window window;
    private boolean init = false;

    /**
     * In 1.13+ {@link net.minecraft.client.gui.screen.Screen#init(Minecraft, int, int)}
     * can be called multiple times
     */
    @Override
    public void init(Minecraft mc, int width, int height) {
        window.width = width;
        window.height = height;
        window.guiLeft = (width - this.xSize) / 2;
        window.guiTop = (height - this.ySize) / 2;
        super.init(mc, width, height);
        if (!init) {
            window.initWindow_();
            init = true;
        }
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        window.width = width;
        window.height = height;
        window.guiLeft = (this.width - this.xSize) / 2;
        window.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public boolean isPauseScreen() {
        return window.doesWindowPauseGame();
    }

    public List<Rectangle2d> getGuiExtraAreas() {
        List<Rectangle2d> ret = Lists.newArrayList();
        window.addExtraGuiAreas(ret);
        return ret;
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, @Nonnull ClickType type) {
        window.handleSlotClick(slotIn == null ? null : ((WindowContainer.WidgetLinkedSlot) slotIn).widget, slotId, mouseButton, type);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        window.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return window.mouseClicked(mouseX, mouseY, mouseButton) || super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    void handleMouseClickDefault(Slot slotIn, int slotId, int mouseButton, @Nonnull ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return window.mouseReleased(mouseX, mouseY, mouseButton);
    }

    boolean mouseReleasedDefault(double mouseX, double mouseY, int mouseButton) {
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return window.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    boolean mouseDraggedDefault(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double dafuq1, double dafuq2, double wheel) {
        boolean b = false;
        if (wheel != 0) {
            MouseHelper mh = Minecraft.getInstance().mouseHelper;
            MainWindow mw = RenderHelper.getMainWindow();
            double mouseX = mh.getMouseX() * width / mw.getFramebufferWidth();//displayWidth;
            double mouseY = height - mh.getMouseY() * height / (mw.getFramebufferHeight() - 1); //Minecraft.getInstance().displayHeight
            b = window.mouseScrolled(wheel, window.translatedMouseX(mouseX), window.translatedMouseY(mouseY));
        }
        return b || super.mouseScrolled(dafuq1, dafuq2, wheel);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        return window.keyPressed(key, scanCode, modifiers) || super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return window.keyReleased(keyCode, scanCode, modifiers) || super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return window.charTyped(typedChar, keyCode) || super.charTyped(typedChar, keyCode);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderSystem.pushMatrix();
        window.drawScreenPre(mouseX, mouseY, partialTicks);
        RenderSystem.popMatrix();
        super.render(mouseX, mouseY, partialTicks);
        RenderSystem.pushMatrix();
        window.drawScreenPost(mouseX, mouseY, partialTicks);
        RenderSystem.popMatrix();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderSystem.pushMatrix();
        window.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderSystem.popMatrix();
    }

    @Override
    protected void renderTooltip(ItemStack stack, int x, int y) {
        List<String> list = InventoryHelper.getTooltip(stack, ElecCore.proxy.getClientPlayer(), Preconditions.checkNotNull(minecraft).gameSettings.advancedItemTooltips);
        window.modifyTooltip(list, ((WindowContainer.WidgetLinkedSlot) hoveredSlot).widget, stack, x, y);
        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().color + list.get(i));
            } else {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }

        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(stack);
        this.renderTooltip(list, x, y, (font == null ? this.font : font));
        net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.pushMatrix();
        window.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        RenderSystem.popMatrix();
    }

}
