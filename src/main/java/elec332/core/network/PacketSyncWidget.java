package elec332.core.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import elec332.core.inventory.IWidgetContainer;
import elec332.core.inventory.widget.Widget;
import elec332.core.util.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class PacketSyncWidget extends AbstractPacket {

    public PacketSyncWidget(){
    }

    public PacketSyncWidget(NBTTagCompound tag, IWidgetContainer widgetContainer, Widget widget){
        super(new NBTHelper().addToTag(new NBTHelper().addToTag(widgetContainer.getWidgets().indexOf(widget), "widget").addToTag(((Container) widgetContainer).windowId, "window").toNBT(), "containerData").addToTag(tag, "data").toNBT());
    }

    @Override
    public IMessage onMessageThreadSafe(AbstractPacket message, MessageContext ctx) {
        NBTTagCompound data = message.networkPackageObject.getCompoundTag("data");
        NBTTagCompound containerData = message.networkPackageObject.getCompoundTag("containerData");
        Container openContainer = Minecraft.getMinecraft().thePlayer.openContainer;
        if (openContainer.windowId == containerData.getInteger("window")){
            ((IWidgetContainer)openContainer).getWidgets().get(containerData.getInteger("widget")).readNBTChangesFromPacket(data, Side.CLIENT);
        }
        return null;
    }
}
