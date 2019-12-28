package elec332.core.inventory.widget;

import com.google.common.collect.Lists;
import elec332.core.ElecCore;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.window.IWidgetContainer;
import elec332.core.inventory.window.Window;
import elec332.core.network.packets.PacketSyncWidget;
import elec332.core.network.packets.PacketWidgetDataToServer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class Widget implements IWidget {

    public Widget(int x, int y, int u, int v, int width, int height) {
        this.x = x;
        this.y = y;
        this.u = u; //For the fontrenderer
        this.v = v; //For the fontrenderer
        this.width = width;
        this.height = height;
    }

    @Override
    public final Widget setContainer(IWidgetContainer container) {
        this.container = container;
        return this;
    }

    @Override
    public final Widget setID(int id) {
        this.id = id;
        return this;
    }

    public Widget setBackground(ResourceLocation background) {
        this.background = background;
        return this;
    }

    private IWidgetContainer container;
    private int id;
    public final int x, y, u, v, width, height;
    private boolean hidden;
    private ResourceLocation background = null;

    @Override
    public void initWidget(IWidgetListener iCrafting) {
        detectAndSendChanges(Lists.newArrayList(iCrafting));
    }

    @Override
    public void detectAndSendChanges(Iterable<IWidgetListener> crafters) {
    }

    public void sendProgressBarUpdate(Iterable<IWidgetListener> crafters, int value) {
        for (IWidgetListener iCrafting : crafters) {
            sendProgressBarUpdate(iCrafting, value);
        }
    }

    public void sendProgressBarUpdate(IWidgetListener iCrafting, int value) {
        iCrafting.sendProgressBarUpdate(value);
    }

    @Override
    public void updateProgressbar(int value) {
    }

    public final void sendNBTChangesToPlayer(ServerPlayerEntity player, CompoundNBT tagCompound) {
        ElecCore.networkHandler.sendTo(new PacketSyncWidget(tagCompound, container, this), player);
    }

    public final void sendNBTChangesToServer(CompoundNBT tagCompound) {
        ElecCore.networkHandler.sendToServer(new PacketWidgetDataToServer(tagCompound, container, this));
    }

    @Override
    public final void readNBTChangesFromPacket(CompoundNBT tagCompound, LogicalSide receiver) {
        if (receiver == LogicalSide.CLIENT) {
            readNBTChangesFromPacket(tagCompound);
        } else {
            readNBTChangesFromPacketServerSide(tagCompound);
        }
    }

    public void readNBTChangesFromPacket(CompoundNBT tagCompound) {
    }

    public void readNBTChangesFromPacketServerSide(CompoundNBT tagCompound) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final boolean isMouseOver(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY, x, y, width, height);
    }

    protected final boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void mouseMoved(double mouseX, double mouseY) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean mouseScrolled(double wheel, double mouseX, double mouseY) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean charTyped(char typedChar, int keyCode) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(Window window, int guiX, int guiY, double mouseX, double mouseY, float partialTicks) {
        if (background != null) {
            RenderHelper.bindTexture(background);
        }
        GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, u, v, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    protected final void bindTexture(ResourceLocation resourceLocation) {
        RenderHelper.bindTexture(resourceLocation);
    }

    protected void drawHollow(int guiX, int guiY, int xPos, int yPos, int width, int height) {
        bindTexture(Window.DEFAULT_BACKGROUND);
        int sX = 180;
        int sY = 23;
        int eX = 218;
        int eY = 113;
        if (width == 18 && height == 18) {
            GuiDraw.drawTexturedModalRect(guiX + x + xPos, guiY + y + yPos, sX, 0, width, height);
        } else {
            float wX = width / 2f;
            float hY = height / 2f;
            int leftX = (int) Math.floor(wX);
            int rightX = (int) Math.ceil(wX);
            int topY = (int) Math.floor(hY);
            int bottomY = (int) Math.ceil(hY);
            if (leftX > 36 || rightX > 36 || topY > 88 || bottomY > 88) {
                throw new UnsupportedOperationException();
            }
            GuiDraw.drawTexturedModalRect(guiX + x + xPos, guiY + y + yPos, sX, sY, leftX, topY);  //Top Left
            GuiDraw.drawTexturedModalRect(guiX + x + xPos + (width - leftX), guiY + y + yPos, eX - rightX, sY, rightX, topY); //Top Right
            GuiDraw.drawTexturedModalRect(guiX + x + xPos, guiY + y + yPos + (height - topY), sX, eY - bottomY, leftX, bottomY); //Bottom Left
            GuiDraw.drawTexturedModalRect(guiX + x + xPos + (width - leftX), guiY + y + yPos + (height - topY), eX - rightX, eY - bottomY, rightX, bottomY); //Bottom Right
        }
    }

    public Widget setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public ToolTip getToolTip(double mouseX, double mouseY) {
        return null;
    }

}
