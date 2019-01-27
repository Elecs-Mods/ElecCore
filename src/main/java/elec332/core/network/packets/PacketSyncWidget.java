package elec332.core.network.packets;

import elec332.core.ElecCore;
import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.inventory.widget.Widget;
import elec332.core.inventory.window.IWidgetContainer;
import elec332.core.inventory.window.Window;
import elec332.core.util.NBTBuilder;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.LogicalSide;

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
    public void onMessageThreadSafe(NBTTagCompound message, IExtendedMessageContext ctx) {
        NBTTagCompound data = message.getCompound("data");
        NBTTagCompound containerData = message.getCompound("containerData");
        Container openContainer = ElecCore.proxy.getClientPlayer().openContainer;
        if (openContainer.windowId == containerData.getInt("window")) {
            ((IWidgetContainer) openContainer).getWidgets().get(containerData.getInt("widget")).readNBTChangesFromPacket(data, LogicalSide.CLIENT);
        }
    }

}
