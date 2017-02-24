package elec332.core.inventory.widget;

import com.google.common.collect.Lists;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.IWidgetContainer;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.window.Window;
import elec332.core.main.ElecCore;
import elec332.core.network.packets.PacketSyncWidget;
import elec332.core.network.packets.PacketWidgetDataToServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public final Widget setContainer(IWidgetContainer container){
        this.container = container;
        return this;
    }

    @Override
    public final Widget setID(int id){
        this.id = id;
        return this;
    }

    private IWidgetContainer container;
    private int id;
    public final int x, y, u, v, width, height;
    private boolean hidden;

    @Override
    public void initWidget(IWidgetListener iCrafting){
        detectAndSendChanges(Lists.newArrayList(iCrafting));
    }

    @Override
    public void detectAndSendChanges(Iterable<IWidgetListener> crafters){
    }

    public void sendProgressBarUpdate(Iterable<IWidgetListener> crafters, int value){
        for (IWidgetListener iCrafting : crafters) {
            sendProgressBarUpdate(iCrafting, value);
        }
    }

    public void sendProgressBarUpdate(IWidgetListener iCrafting, int value){
        iCrafting.sendProgressBarUpdate(value);
    }

    @Override
    public void updateProgressbar(int value){
    }

    public final void sendNBTChangesToPlayer(EntityPlayerMP player, NBTTagCompound tagCompound){
        ElecCore.networkHandler.sendTo(new PacketSyncWidget(tagCompound, container, this), player);
    }

    public final void sendNBTChangesToServer(NBTTagCompound tagCompound){
        ElecCore.networkHandler.sendToServer(new PacketWidgetDataToServer(tagCompound, container, this));
    }

    @Override
    public final void readNBTChangesFromPacket(NBTTagCompound tagCompound, Side receiver){
        if (receiver == Side.CLIENT){
            readNBTChangesFromPacket(tagCompound);
        } else {
            readNBTChangesFromPacketServerSide(tagCompound);
        }
    }

    public void readNBTChangesFromPacket(NBTTagCompound tagCompound){
    }

    public void readNBTChangesFromPacketServerSide(NBTTagCompound tagCompound){
    }

    @Override
    public final boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x - 1 && mouseX < x + width + 1 && mouseY >= y - 1 && mouseY < y + height + 1;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Window window, int guiX, int guiY, int mouseX, int mouseY) {
        GuiDraw.drawTexturedModalRect(guiX + x, guiY + y, u, v, width, height);
    }

    @SideOnly(Side.CLIENT)
    protected final void bindTexture(ResourceLocation resourceLocation){
        RenderHelper.bindTexture(resourceLocation);
    }

    public Widget setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public boolean isHidden(){
        return hidden;
    }

    @Override
    public ToolTip getToolTip(){
        return null;
    }

}
