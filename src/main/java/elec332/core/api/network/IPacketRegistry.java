package elec332.core.api.network;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 15-10-2016.
 * <p>
 * A packet registry
 */
public interface IPacketRegistry extends IPacketRegistryContainer {

    /**
     * Registers a packet to this packet registry
     *
     * @param packetClass The packet class
     */
    default public <T extends IMessage & BiConsumer<T, Supplier<IExtendedMessageContext>>> void registerPacket(Class<T> packetClass) {
        registerPacket(packetClass, packetClass);
    }

    /**
     * Registers a packet to this packet registry
     *
     * @param type The packet type
     */
    @SuppressWarnings("unchecked")
    default public <T extends IMessage & BiConsumer<T, Supplier<IExtendedMessageContext>>> void registerPacket(T type) {
        registerPacket(type, (Class<T>) type.getClass());
    }

    /**
     * Registers a packet to this packet registry
     *
     * @param messageHandler The class of the message handler
     * @param messageType    The message class
     */
    default public <M extends IMessage> void registerPacket(Class<? extends BiConsumer<M, Supplier<IExtendedMessageContext>>> messageHandler, Class<M> messageType) {
        try {
            registerPacket(messageHandler.newInstance(), messageType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a packet to this packet registry
     *
     * @param messageHandler The message handler
     * @param messageType    The message class
     */
    public <M extends IMessage> void registerPacket(BiConsumer<M, Supplier<IExtendedMessageContext>> messageHandler, Class<M> messageType);

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
