package elec332.core.network.packets;

import elec332.core.ElecCore;
import elec332.core.inventory.widget.Widget;
import elec332.core.inventory.window.IWidgetContainer;
import elec332.core.inventory.window.Window;
import elec332.core.util.NBTBuilder;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Elec332 on 31-7-2015.
 */
@SuppressWarnings("unused")
public class PacketSyncWidget extends AbstractPacket {

    public PacketSyncWidget() {
    }

    public PacketSyncWidget(NBTTagCompound tag, IWidgetContainer widgetContainer, Widget widget) {
        super(new NBTBuilder().setTag("containerData", new NBTBuilder().setInteger("widget", widgetContainer.getWidgets().indexOf(widget)).setInteger("window", ((Window) widgetContainer).getWindowID()).serializeNBT()).setTag("data", tag).serializeNBT());
    }

    @Override
    public IMessage onMessageThreadSafe(AbstractPacket message, MessageContext ctx) {
        NBTTagCompound data = message.networkPackageObject.getCompoundTag("data");
        NBTTagCompound containerData = message.networkPackageObject.getCompoundTag("containerData");
        Container openContainer = ElecCore.proxy.getClientPlayer().openContainer;
        if (openContainer.windowId == containerData.getInteger("window")) {
            ((IWidgetContainer) openContainer).getWidgets().get(containerData.getInteger("widget")).readNBTChangesFromPacket(data, Side.CLIENT);
        }
        return null;
    }

}
