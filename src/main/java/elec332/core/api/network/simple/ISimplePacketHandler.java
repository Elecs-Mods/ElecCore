package elec332.core.api.network.simple;

import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.IExtendedMessageContext;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface ISimplePacketHandler {

    public void onPacket(ElecByteBuf data, IExtendedMessageContext messageContext, ISimpleNetworkPacketManager networkHandler);

}
