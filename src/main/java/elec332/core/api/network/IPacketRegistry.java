package elec332.core.api.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Elec332 on 15-10-2016.
 */
public interface IPacketRegistry extends IPacketRegistryContainer {

    default public <T extends IMessage & IMessageHandler<T, M>, M extends IMessage> void registerPacket(Class<T> packetClass, Side side) {
        registerPacket(packetClass, packetClass, side);
    }

    @SuppressWarnings("unchecked")
    default public <T extends IMessage & IMessageHandler<T, M>, M extends IMessage> void registerPacket(T type, Side side) {
        registerPacket(type, (Class<T>) type.getClass(), side);
    }

    default public <M extends IMessage, R extends IMessage> void registerPacket(Class<? extends IMessageHandler<M, R>> messageHandler, Class<M> messageType, Side side) {
        try {
            registerPacket(messageHandler.newInstance(), messageType, side);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <M extends IMessage, R extends IMessage> void registerPacket(IMessageHandler<M, R> messageHandler, Class<M> messageType, Side side);

    @Override
    public void registerPacketsTo(IPacketRegistry packetRegistry);

    default public void importFrom(IPacketRegistryContainer packetRegistryContainer) {
        if (packetRegistryContainer != this) {
            packetRegistryContainer.registerPacketsTo(this);
        }
    }

}
