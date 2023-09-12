package elec332.core.network.packets;

import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.api.network.simple.ISimplePacket;
import elec332.core.api.network.simple.ISimplePacketHandler;
import elec332.core.inventory.window.WindowContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 3-12-2016.
 */
public class PacketWindowData implements ISimplePacket, ISimplePacketHandler {

    public PacketWindowData(WindowContainer container, CompoundTag tag) {
        this.tag = tag;
        this.window = container.windowId;
    }

    private final CompoundTag tag;
    private final int window;

    @Override
    public void toBytes(ElecByteBuf byteBuf) {
        byteBuf.writeInt(window);
        byteBuf.writeCompoundTagToBuffer(tag);
    }

    @Nullable
    @Override
    public ISimplePacketHandler getPacketHandler() {
        return this;
    }

    @Override
    public void onPacket(ElecByteBuf data, IExtendedMessageContext messageContext, ISimpleNetworkPacketManager networkHandler) {
        Container container = messageContext.getSender().openContainer;
        if (container.windowId == data.readInt() && container instanceof WindowContainer) {
            ((WindowContainer) container).getWindow().onPacket(data.readCompoundTagFromBuffer(), messageContext.getReceptionSide());
        }
    }

}
