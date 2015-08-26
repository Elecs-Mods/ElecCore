package elec332.core.inventory.widget;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.client.render.InventoryRenderHelper;
import elec332.core.inventory.IWidgetContainer;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.main.ElecCore;
import elec332.core.network.PacketSyncWidget;
import elec332.core.network.PacketWidgetDataToServer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

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

    public void initWidget(ICrafting iCrafting){
        detectAndSendChanges(Lists.newArrayList(iCrafting));
    }

    public void detectAndSendChanges(List<ICrafting> crafters){
    }

    public void sendProgressBarUpdate(List<ICrafting> crafters, int value){
        for (ICrafting iCrafting : crafters)
            iCrafting.sendProgressBarUpdate((Container)container, id, value);
    }

    public void sendProgressBarUpdate(ICrafting iCrafting, int value){
        iCrafting.sendProgressBarUpdate((Container)container, id, value);
    }

    public void updateProgressbar(int value){
    }

    public final void sendNBTChangesToPlayer(EntityPlayerMP player, NBTTagCompound tagCompound){
        ElecCore.networkHandler.getNetworkWrapper().sendTo(new PacketSyncWidget(tagCompound, container, this), player);
    }

    public final void sendNBTChangesToServer(NBTTagCompound tagCompound){
        ElecCore.networkHandler.getNetworkWrapper().sendToServer(new PacketWidgetDataToServer(tagCompound, container, this));
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
    protected void bindTexture(ResourceLocation resourceLocation){
        InventoryRenderHelper.bindTexture(resourceLocation);
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

    protected boolean nullityDiffers(Object o1, Object o2){
        return o1 == null && o2 == null || o1 != null && o2 != null;
    }

}
