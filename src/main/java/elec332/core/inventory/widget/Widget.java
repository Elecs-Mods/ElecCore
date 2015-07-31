package elec332.core.inventory.widget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.inventory.IWidgetContainer;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.main.ElecCore;
import elec332.core.network.PacketSyncWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;

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

    private IWidgetContainer container;
    public final int x, y, u, v, width, height;
    private boolean hidden;

    public void initWidget(ICrafting iCrafting){
    }

    public void detectAndSendChanges(ICrafting iCrafting){
    }

    public final void sendNBTChangesToPlayer(EntityPlayerMP player, NBTTagCompound tagCompound){
        ElecCore.networkHandler.getNetworkWrapper().sendTo(new PacketSyncWidget(tagCompound, container, this), player);
    }

    public void readNBTChangesFromPacket(NBTTagCompound tagCompound){
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
