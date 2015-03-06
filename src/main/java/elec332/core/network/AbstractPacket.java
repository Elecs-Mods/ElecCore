package elec332.core.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Created by Elec332 on 23-2-2015.
 */
public abstract class AbstractPacket implements IMessage, IMessageHandler<AbstractPacket, IMessage> {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public Object networkPackageObject;

    @Override
    public abstract IMessage onMessage(AbstractPacket message, MessageContext ctx);
}
