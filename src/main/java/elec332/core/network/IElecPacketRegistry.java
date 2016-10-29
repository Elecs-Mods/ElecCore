package elec332.core.network;

import elec332.core.api.network.IPacketRegistry;
import elec332.core.network.packets.AbstractPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Elec332 on 15-10-2016.
 */
public interface IElecPacketRegistry extends IPacketRegistry {

    @SuppressWarnings("unchecked")
    default public <T extends AbstractPacket> void registerServerPacket(Class<T> packetClass){
        registerPacket((Class<? extends IMessageHandler<T, IMessage>>)packetClass, packetClass, Side.CLIENT);
    }

    @SuppressWarnings("unchecked")
    default public <T extends AbstractPacket> void registerClientPacket(Class<T> packetClass){
        registerPacket((Class<? extends IMessageHandler<T, IMessage>>)packetClass, packetClass, Side.CLIENT);
    }

    default public void registerServerPacket(AbstractPacket packet){
        registerPacket(packet, Side.SERVER);
    }

    default public void registerClientPacket(AbstractPacket packet){
        registerPacket(packet, Side.CLIENT);
    }

    @Override
    public <M extends IMessage, R extends IMessage> void registerPacket(IMessageHandler<M, R> messageHandler, Class<M> messageType, Side side);

}
