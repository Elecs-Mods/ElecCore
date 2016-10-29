package elec332.core.network.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.api.network.IPacketDispatcher;
import elec332.core.api.network.IPacketRegistry;
import elec332.core.api.network.object.*;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.api.network.simple.ISimplePacket;
import elec332.core.api.network.simple.ISimplePacketHandler;
import elec332.core.network.IElecNetworkHandler;
import gnu.trove.map.hash.TByteObjectHashMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Elec332 on 23-2-2015.
 */
class DefaultNetworkHandler implements IElecNetworkHandler {

    DefaultNetworkHandler(SimpleNetworkWrapper networkWrapper){
        this.networkWrapper = networkWrapper;
        this.channelName = getCurrentNameFrom(networkWrapper);
        this.i = getCurrentIndexFrom(networkWrapper);
        this.networkObjectManager = new DefaultNetworkObjectManager(this);
        this.simpleNetworkPacketManager = new DefaultSimpleNetworkHandler(this, channelName);
        this.packetManagers = Maps.newHashMap();
    }

    DefaultNetworkHandler(String channelName){
        this.channelName = channelName.toLowerCase();
        this.networkWrapper = new SimpleNetworkWrapper(this.channelName);
        this.i = 0;
        this.networkObjectManager = new DefaultNetworkObjectManager(this);
        this.simpleNetworkPacketManager = new DefaultSimpleNetworkHandler(this, channelName);
        this.packetManagers = Maps.newHashMap();
    }

    private final SimpleNetworkWrapper networkWrapper;
    private final INetworkObjectManager networkObjectManager;
    private final ISimpleNetworkPacketManager simpleNetworkPacketManager;
    private final Map<String, ISimpleNetworkPacketManager> packetManagers;
    private final String channelName;
    private int i;

    @Nonnull
    ISimpleNetworkPacketManager getSimpleNetworkManager(String s){
        if (s.equals(channelName)){
            return simpleNetworkPacketManager;
        }
        ISimpleNetworkPacketManager ret = packetManagers.get(s);
        if (ret == null){
            packetManagers.put(s, ret = new DefaultSimpleNetworkHandler(this, s));
        }
        return Preconditions.checkNotNull(ret);
    }

    @Override
    public <M extends IMessage, R extends IMessage> void registerPacket(IMessageHandler<M, R> messageHandler, Class<M> messageType, Side side){
        networkWrapper.registerMessage(messageHandler, messageType, i, side);
        ++i;
    }

    @Override
    public void registerPacketsTo(IPacketRegistry packetRegistry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <N extends INetworkObjectReceiver> INetworkObjectHandler<?> registerNetworkObject(N networkObject) {
        return networkObjectManager.registerNetworkObject(networkObject);
    }

    @Override
    public <R extends INetworkObjectReceiver, S extends INetworkObjectSender> INetworkObjectHandler<S> registerNetworkObject(@Nullable R networkObjectR, @Nullable S networkObjectS) {
        return networkObjectManager.registerNetworkObject(networkObjectR, networkObjectS);
    }

    @Override
    public <N extends INetworkObject> INetworkObjectHandler<N> registerSpecialNetworkObject(N networkObject) {
        return networkObjectManager.registerSpecialNetworkObject(networkObject);
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public void sendToAll(IMessage message) {
        networkWrapper.sendToAll(message);
    }

    @Override
    public void sendTo(IMessage message, EntityPlayerMP player) {
        networkWrapper.sendTo(message, player);
    }

    @Override
    public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        networkWrapper.sendToAllAround(message, point);
    }

    @Override
    public void sendToDimension(IMessage message, int dimensionId) {
        networkWrapper.sendToDimension(message, dimensionId);
    }

    @Override
    public void sendToServer(IMessage message) {
        networkWrapper.sendToServer(message);
    }

    //Unfortunately I had to write this...

    @SuppressWarnings("unchecked")
    private static int getCurrentIndexFrom(SimpleNetworkWrapper networkWrapper){
        try {
            FMLIndexedMessageToMessageCodec c = (FMLIndexedMessageToMessageCodec) codec.get(networkWrapper);
            byte[] ids = ((TByteObjectHashMap) discriminators.get(c)).keys();
            int ret = 0;
            for (byte b : ids){
                ret = Math.max(ret, b);
            }
            return ret;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static String getCurrentNameFrom(SimpleNetworkWrapper networkWrapper) {
        FMLEmbeddedChannel ch = getNetworkChannels(networkWrapper).get(Side.SERVER);
        Map<String, FMLEmbeddedChannel> map = getRegistryChannels().get(Side.SERVER);
        for (Map.Entry<String, FMLEmbeddedChannel> entry : map.entrySet()) {
            if (entry.getValue() == ch) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Error finding name! Channel not found!");
    }

    @SuppressWarnings("unchecked")
    static EnumMap<Side, Map<String, FMLEmbeddedChannel>> getRegistryChannels(){
        try {
            return (EnumMap<Side, Map<String, FMLEmbeddedChannel>>) registryChannels.get(NetworkRegistry.INSTANCE);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    static EnumMap<Side, FMLEmbeddedChannel> getNetworkChannels(SimpleNetworkWrapper wrapper){
        try {
            return (EnumMap<Side, FMLEmbeddedChannel>) wrapperChannels.get(wrapper);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static final Field codec, discriminators, wrapperChannels, registryChannels;

    static {
        try {
            codec = SimpleNetworkWrapper.class.getDeclaredField("packetCodec");
            discriminators = FMLIndexedMessageToMessageCodec.class.getDeclaredField("discriminators");

            wrapperChannels = SimpleNetworkWrapper.class.getDeclaredField("channels");
            registryChannels = NetworkRegistry.class.getDeclaredField("channels");
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendToAll(ISimplePacket message) {
        simpleNetworkPacketManager.sendToAll(message);
    }

    @Override
    public void sendTo(ISimplePacket message, EntityPlayerMP player) {
        simpleNetworkPacketManager.sendTo(message, player);
    }

    @Override
    public void sendToAllAround(ISimplePacket message, NetworkRegistry.TargetPoint point) {
        simpleNetworkPacketManager.sendToAllAround(message, point);
    }

    @Override
    public void sendToDimension(ISimplePacket message, int dimensionId) {
        simpleNetworkPacketManager.sendToDimension(message, dimensionId);
    }

    @Override
    public void sendToServer(ISimplePacket message) {
        simpleNetworkPacketManager.sendToServer(message);
    }

    @Override
    public void sendToAll(ISimplePacket message, ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.sendToAll(message, packetHandler);
    }

    @Override
    public void sendTo(ISimplePacket message, ISimplePacketHandler packetHandler, EntityPlayerMP player) {
        simpleNetworkPacketManager.sendTo(message, packetHandler, player);
    }

    @Override
    public void sendToAllAround(ISimplePacket message, ISimplePacketHandler packetHandler, NetworkRegistry.TargetPoint point) {
        simpleNetworkPacketManager.sendToAllAround(message, packetHandler, point);
    }

    @Override
    public void sendToDimension(ISimplePacket message, ISimplePacketHandler packetHandler, int dimensionId) {
        simpleNetworkPacketManager.sendToDimension(message, packetHandler, dimensionId);
    }

    @Override
    public void sendToServer(ISimplePacket message, ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.sendToServer(message);
    }

    @Override
    public void sendToAll(ByteBuf data, ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.sendToAll(data, packetHandler);
    }

    @Override
    public void sendTo(ByteBuf data, ISimplePacketHandler packetHandler, EntityPlayerMP player) {
        simpleNetworkPacketManager.sendTo(data, packetHandler, player);
    }

    @Override
    public void sendToAllAround(ByteBuf data, ISimplePacketHandler packetHandler, NetworkRegistry.TargetPoint point) {
        simpleNetworkPacketManager.sendToAllAround(data, packetHandler, point);
    }

    @Override
    public void sendToDimension(ByteBuf data, ISimplePacketHandler packetHandler, int dimensionId) {
        simpleNetworkPacketManager.sendToDimension(data, packetHandler, dimensionId);
    }

    @Override
    public void sendToServer(ByteBuf data, ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.sendToServer(data, packetHandler);
    }

    @Override
    public void registerPacket(Class<ISimplePacket> packetType) {
        simpleNetworkPacketManager.registerPacket(packetType);
    }

    @Override
    public void registerPacket(ISimplePacket packet) {
        simpleNetworkPacketManager.registerPacket(packet);
    }

    @Override
    public void registerPacketHandler(ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.registerPacketHandler(packetHandler);
    }

    @Override
    public void registerPacket(Class<ISimplePacket> packetType, ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.registerPacket(packetType, packetHandler);
    }

    @Override
    public void registerPacket(ISimplePacket packet, ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.registerPacket(packet, packetHandler);
    }

    @Override
    public IPacketDispatcher getPacketDispatcher() {
        return simpleNetworkPacketManager.getPacketDispatcher();
    }

}
