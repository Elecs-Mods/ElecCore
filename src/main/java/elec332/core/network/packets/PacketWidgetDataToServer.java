package elec332.core.network.packets;

import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.inventory.widget.Widget;
import elec332.core.inventory.window.IWidgetContainer;
import elec332.core.inventory.window.Window;
import elec332.core.inventory.window.WindowManager;
import elec332.core.util.NBTBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by Elec332 on 23-8-2015.
 */
@SuppressWarnings("unused")
public class PacketWidgetDataToServer extends AbstractPacket {

    public PacketWidgetDataToServer() {
    }

    public PacketWidgetDataToServer(NBTTagCompound tag, IWidgetContainer widgetContainer, Widget widget) {
        super(new NBTBuilder().setTag("containerData", new NBTBuilder().setInteger("widget", widgetContainer.getWidgets().indexOf(widget)).setInteger("window", ((Window) widgetContainer).getWindowID()).serializeNBT()).setTag("data", tag).serializeNBT());
    }

    @Override
    public void onMessageThreadSafe(NBTTagCompound message, IExtendedMessageContext ctx) {
        NBTTagCompound data = message.getCompound("data");
        NBTTagCompound containerData = message.getCompound("containerData");
        Window window = WindowManager.getOpenWindow(ctx.getSender(), containerData.getInt("window"));
        if (window != null) {
            window.getWidgets().get(containerData.getInt("widget")).readNBTChangesFromPacket(data, LogicalSide.SERVER);
        }
    }

}
