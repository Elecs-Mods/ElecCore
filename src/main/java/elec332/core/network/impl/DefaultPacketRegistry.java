package elec332.core.network.impl;

import com.google.common.collect.Lists;
import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.api.network.IMessage;
import elec332.core.api.network.IPacketRegistry;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 23-10-2016.
 */
class DefaultPacketRegistry implements IPacketRegistry {

    DefaultPacketRegistry() {
        this.registeredPackets = Lists.newArrayList();
    }

    private final List<Wrapper<?>> registeredPackets;

    @Override
    @SuppressWarnings("unchecked")
    public <M extends IMessage> void registerPacket(BiConsumer<M, Supplier<IExtendedMessageContext>> messageHandler, Class<M> messageType) {
        registeredPackets.add(new Wrapper(messageHandler, messageType));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerPacketsTo(IPacketRegistry packetRegistry) {
        for (Wrapper wrapper : registeredPackets) {
            packetRegistry.registerPacket(wrapper.messageHandler, wrapper.messageType);
        }
    }

    private class Wrapper<M extends IMessage> {

        private Wrapper(BiConsumer<M, Supplier<IExtendedMessageContext>> messageHandler, Class<M> messageType) {
            this.messageHandler = messageHandler;
            this.messageType = messageType;
        }

        private final BiConsumer<M, Supplier<IExtendedMessageContext>> messageHandler;
        private final Class<M> messageType;

    }

}
