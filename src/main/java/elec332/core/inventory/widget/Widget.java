package elec332.core.inventory.widget;

import com.google.common.collect.Lists;
import elec332.core.client.RenderHelper;
import elec332.core.inventory.IWidgetContainer;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.main.ElecCore;
import elec332.core.network.packets.PacketSyncWidget;
import elec332.core.network.packets.PacketWidgetDataToServer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class Widget {

    public Widget(int x, int y, int u, int v, int width, int height) {
        this.x = x;
        this.y = y;
        this.u = u; //For the fontrenderer
        this.v = v; //For the fontrenderer
        this.width = width;
        this.height = height;
    }

    public final Widget setContainer(IWidgetContainer container){
        this.container = container;
        return this;
    }

    public final Widget setID(int id){
        this.id = id;
        return this;
    }

    private IWidgetContainer container;
    private int id;
    public final int x, y, u, v, width, height;
    private boolean hidden;

    public void initWidget(IContainerListener iCrafting){
        detectAndSendChanges(Lists.newArrayList(iCrafting));
    }

    public void detectAndSendChanges(List<IContainerListener> crafters){
    }

    public void sendProgressBarUpdate(List<IContainerListener> crafters, int value){
        for (IContainerListener iCrafting : crafters)
            iCrafting.sendProgressBarUpdate((Container)container, id, value);
    }

    public void sendProgressBarUpdate(IContainerListener iCrafting, int value){
        iCrafting.sendProgressBarUpdate((Container)container, id, value);
    }

    public void updateProgressbar(int value){
    }

    public final void sendNBTChangesToPlayer(EntityPlayerMP player, NBTTagCompound tagCompound){
        ElecCore.networkHandler.sendTo(new PacketSyncWidget(tagCompound, container, this), player);
    }

    public final void sendNBTChangesToServer(NBTTagCompound tagCompound){
        ElecCore.networkHandler.sendToServer(new PacketWidgetDataToServer(tagCompound, container, this));
    }

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

    public final boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x - 1 && mouseX < x + width + 1 && mouseY >= y - 1 && mouseY < y + height + 1;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void draw(Gui gui, int guiX, int guiY, int mouseX, int mouseY) {
        gui.drawTexturedModalRect(guiX + x, guiY + y, u, v, width, height);
    }

    @SideOnly(Side.CLIENT)
    protected final void bindTexture(ResourceLocation resourceLocation){
        RenderHelper.bindTexture(resourceLocation);
    }

    public Widget setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public boolean isHidden(){
        return hidden;
    }

    public ToolTip getToolTip(){
        return null;
    }

}
