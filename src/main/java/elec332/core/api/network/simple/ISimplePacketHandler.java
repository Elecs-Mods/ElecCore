package elec332.core.api.network.simple;

import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.IExtendedMessageContext;

/**
 * Created by Elec332 on 23-10-2016.
 * <p>
 * Packet-handler for simple packets, can be used for multiple packets.
 * A packet can also have a custom packet-handler chosen by the sender.
 */
public interface ISimplePacketHandler {

    public void onPacket(ElecByteBuf data, IExtendedMessageContext messageContext, ISimpleNetworkPacketManager networkHandler);

}
