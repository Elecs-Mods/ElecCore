package elec332.core.network.packets;

import elec332.core.inventory.widget.Widget;
import elec332.core.inventory.window.IWidgetContainer;
import elec332.core.inventory.window.Window;
import elec332.core.inventory.window.WindowManager;
import elec332.core.util.NBTBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

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
    public IMessage onMessageThreadSafe(NBTTagCompound message, MessageContext ctx) {
        NBTTagCompound data = message.getCompoundTag("data");
        NBTTagCompound containerData = message.getCompoundTag("containerData");
        Window window = WindowManager.getOpenWindow(ctx.getServerHandler().player, containerData.getInteger("window"));
        if (window != null) {
            window.getWidgets().get(containerData.getInteger("widget")).readNBTChangesFromPacket(data, Side.SERVER);
        }
        return null;
    }

}
