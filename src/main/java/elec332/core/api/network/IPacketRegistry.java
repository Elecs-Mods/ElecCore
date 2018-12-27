package elec332.core.api.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Elec332 on 15-10-2016.
 *
 * A packet registry
 */
public interface IPacketRegistry extends IPacketRegistryContainer {

    /**
     * Registers a packet to this packet registry
     *
     * @param packetClass The packet class
     * @param side the side for the packet handler
     */
    default public <T extends IMessage & IMessageHandler<T, M>, M extends IMessage> void registerPacket(Class<T> packetClass, Side side) {
        registerPacket(packetClass, packetClass, side);
    }

    /**
     * Registers a packet to this packet registry
     *
     * @param type The packet type
     * @param side the side for the packet handler
     */
    @SuppressWarnings("unchecked")
    default public <T extends IMessage & IMessageHandler<T, M>, M extends IMessage> void registerPacket(T type, Side side) {
        registerPacket(type, (Class<T>) type.getClass(), side);
    }

    /**
     * Registers a packet to this packet registry
     *
     * @param messageHandler The class of the message handler
     * @param messageType The message class
     * @param side the side for the packet handler
     */
    default public <M extends IMessage, R extends IMessage> void registerPacket(Class<? extends IMessageHandler<M, R>> messageHandler, Class<M> messageType, Side side) {
        try {
            registerPacket(messageHandler.newInstance(), messageType, side);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a packet to this packet registry
     *
     * @param messageHandler The message handler
     * @param messageType The message class
     * @param side the side for the packet handler
     */
    public <M extends IMessage, R extends IMessage> void registerPacket(IMessageHandler<M, R> messageHandler, Class<M> messageType, Side side);

    /**
     * Gets called when this container is supposed to register
     * its packets to the provided {@link IPacketRegistry}
     *
     * @param packetRegistry The packet registry to register the packets to
     */
    @Override
    public void registerPacketsTo(IPacketRegistry packetRegistry);

    /**
     * Imports packets from a {@link IPacketRegistryContainer},
     * making the container register its packets to this registry
     *
     * @param packetRegistryContainer The packet container
     */
    default public void importFrom(IPacketRegistryContainer packetRegistryContainer) {
        if (packetRegistryContainer != this) {
            packetRegistryContainer.registerPacketsTo(this);
        }
    }

}
