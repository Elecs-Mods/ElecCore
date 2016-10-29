package elec332.core.api.network.simple;

import elec332.core.api.network.ElecByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface ISimplePacketHandler {

    public void onPacket(ElecByteBuf data, MessageContext messageContext, ISimpleNetworkPacketManager networkHandler);

}
