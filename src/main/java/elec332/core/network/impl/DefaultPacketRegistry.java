package elec332.core.network.impl;

import com.google.common.collect.Lists;
import elec332.core.api.network.IPacketRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/**
 * Created by Elec332 on 23-10-2016.
 */
class DefaultPacketRegistry implements IPacketRegistry {

    DefaultPacketRegistry(){
        this.registeredPackets = Lists.newArrayList();
    }

    private final List<Wrapper<?, ?>> registeredPackets;

    @Override
    @SuppressWarnings("unchecked")
    public <M extends IMessage, R extends IMessage> void registerPacket(IMessageHandler<M, R> messageHandler, Class<M> messageType, Side side) {
        registeredPackets.add(new Wrapper(messageHandler, messageType, side));
    }

    @Override
    public void registerPacketsTo(IPacketRegistry packetRegistry) {
        for (Wrapper wrapper : registeredPackets){
            packetRegistry.registerPacket(wrapper.messageHandler, wrapper.messageType, wrapper.side);
        }
    }

    private class Wrapper<M extends IMessage, R extends IMessage> {

        private Wrapper(IMessageHandler<M, R> messageHandler, Class<M> messageType, Side side){
            this.messageHandler = messageHandler;
            this.messageType = messageType;
            this.side = side;
        }

        private final IMessageHandler<M, R> messageHandler;
        private final Class<M> messageType;
        private final Side side;

    }

}
