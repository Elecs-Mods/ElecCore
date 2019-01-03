package elec332.core.network;

import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.api.network.IMessage;
import elec332.core.api.network.IPacketRegistry;
import elec332.core.network.packets.AbstractPacket;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 15-10-2016.
 */
public interface IElecPacketRegistry extends IPacketRegistry {

    /**
     * Registers a packet to this packet registry
     *
     * @param packetClass The packet class
     */
    @SuppressWarnings("unchecked")
    default public <T extends AbstractPacket> void registerAbstractPacket(Class<T> packetClass) {
        registerPacket((Class<? extends BiConsumer<T, Supplier<IExtendedMessageContext>>>) packetClass, packetClass);
    }

    /**
     * Registers a packet to this packet registry
     * The packet handler should be on the server side
     *
     * @param packet The packet type
     */
    default public void registerAbstractPacket(AbstractPacket packet) {
        registerPacket(packet);
    }

    /**
     * Registers a packet to this packet registry
     *
     * @param messageHandler The message handler
     * @param messageType    The message class
     */
    @Override
    public <M extends IMessage> void registerPacket(BiConsumer<M, Supplier<IExtendedMessageContext>> messageHandler, Class<M> messageType);

}
