package elec332.core.effects.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class PacketSyncAbilities extends AbstractPacket{


    @Override
    public IMessage onMessage(AbstractPacket message, MessageContext ctx) {
        return null;
    }
}
