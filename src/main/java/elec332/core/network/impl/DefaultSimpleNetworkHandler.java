package elec332.core.network.impl;

import com.google.common.base.Preconditions;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.INetworkHandler;
import elec332.core.api.network.IPacketDispatcher;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.api.network.simple.ISimplePacket;
import elec332.core.api.network.simple.ISimplePacketHandler;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Elec332 on 23-10-2016.
 */
class DefaultSimpleNetworkHandler implements ISimpleNetworkPacketManager, IMessageHandler<DefaultSimpleNetworkHandler.PacketSimplePacket, IMessage> {

    @SuppressWarnings("all")
    DefaultSimpleNetworkHandler(INetworkHandler networkHandler, String s){
        this.idToHandler = new TIntObjectHashMap<ISimplePacketHandler>();
        this.handlerToId = new TObjectIntHashMap<ISimplePacketHandler>();
        this.packetToId = new TObjectIntHashMap<Class<? extends ISimplePacket>>();
        this.b = 0;
        for (Side side : Side.values()){
            networkHandler.registerPacket(this, PacketSimplePacket.class, side);
        }
        this.packetDispatcher = networkHandler;
        this.s = s;
    }

    private final IPacketDispatcher packetDispatcher;
    private final String s;
    private TIntObjectMap<ISimplePacketHandler> idToHandler;
    private TObjectIntMap<ISimplePacketHandler> handlerToId;
    private TObjectIntMap<Class<? extends ISimplePacket>> packetToId;
    private byte b;

    @Override
    public String getChannelName() {
        return s;
    }

    @Override
    public void sendToAll(ISimplePacket message) {
        packetDispatcher.sendToAll(from(message));
    }

    @Override
    public void sendTo(ISimplePacket message, EntityPlayerMP player) {
        packetDispatcher.sendTo(from(message), player);
    }

    @Override
    public void sendToAllAround(ISimplePacket message, NetworkRegistry.TargetPoint point) {
        packetDispatcher.sendToAllAround(from(message), point);
    }

    @Override
    public void sendToDimension(ISimplePacket message, int dimensionId) {
        packetDispatcher.sendToDimension(from(message), dimensionId);
    }

    @Override
    public void sendToServer(ISimplePacket message) {
        packetDispatcher.sendToServer(from(message));
    }

    @Override
    public void sendToAll(ISimplePacket message, ISimplePacketHandler packetHandler) {
        packetDispatcher.sendToAll(from(message, packetHandler));
    }

    @Override
    public void sendTo(ISimplePacket message, ISimplePacketHandler packetHandler, EntityPlayerMP player) {
        packetDispatcher.sendTo(from(message, packetHandler), player);
    }

    @Override
    public void sendToAllAround(ISimplePacket message, ISimplePacketHandler packetHandler, NetworkRegistry.TargetPoint point) {
        packetDispatcher.sendToAllAround(from(message, packetHandler), point);
    }

    @Override
    public void sendToDimension(ISimplePacket message, ISimplePacketHandler packetHandler, int dimensionId) {
        packetDispatcher.sendToDimension(from(message, packetHandler), dimensionId);
    }

    @Override
    public void sendToServer(ISimplePacket message, ISimplePacketHandler packetHandler) {
        packetDispatcher.sendToServer(from(message, packetHandler));
    }

    @Override
    public void sendToAll(ByteBuf data, ISimplePacketHandler packetHandler) {
        packetDispatcher.sendToAll(from(data, packetHandler));
    }

    @Override
    public void sendTo(ByteBuf data, ISimplePacketHandler packetHandler, EntityPlayerMP player) {
        packetDispatcher.sendTo(from(data, packetHandler), player);
    }

    @Override
    public void sendToAllAround(ByteBuf data, ISimplePacketHandler packetHandler, NetworkRegistry.TargetPoint point) {
        packetDispatcher.sendToAllAround(from(data, packetHandler), point);
    }

    @Override
    public void sendToDimension(ByteBuf data, ISimplePacketHandler packetHandler, int dimensionId) {
        packetDispatcher.sendToDimension(from(data, packetHandler), dimensionId);
    }

    @Override
    public void sendToServer(ByteBuf data, ISimplePacketHandler packetHandler) {
        packetDispatcher.sendToServer(from(data, packetHandler));
    }

    private PacketSimplePacket from(ISimplePacket message){
        return new PacketSimplePacket(packetToId.get(message.getClass()), fetchData(message));
    }

    private PacketSimplePacket from(ISimplePacket message, ISimplePacketHandler handler){
        return new PacketSimplePacket(handlerToId.get(handler), fetchData(message));
    }

    private PacketSimplePacket from(ByteBuf data, ISimplePacketHandler handler){
        return new PacketSimplePacket(handlerToId.get(handler), data);
    }

    private ByteBuf fetchData(ISimplePacket packet){
        ElecByteBuf ret = new ElecByteBufImpl(Unpooled.buffer());
        packet.toBytes(ret);
        return ret;
    }

    @Override
    public void registerPacket(Class<? extends ISimplePacket> packet) {
        try {
            registerPacket(packet.newInstance());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerPacket(ISimplePacket packet) {
        ISimplePacketHandler handler = packet.getPacketHandler();
        if (handler == null){
            throw new UnsupportedOperationException();
        }
        registerPacket(packet, handler);
    }

    @Override
    public void registerPacketHandler(ISimplePacketHandler packetHandler) {
        Preconditions.checkNotNull(packetHandler);
        idToHandler.put(b, packetHandler);
        handlerToId.put(packetHandler, b);
        b++;
    }

    @Override
    public void registerPacket(Class<? extends ISimplePacket> packet, ISimplePacketHandler packetHandler) {
        try {
            registerPacket(packet.newInstance(), packetHandler);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerPacket(ISimplePacket packet, ISimplePacketHandler packetHandler) {
        Preconditions.checkNotNull(packet);
        Preconditions.checkNotNull(packetHandler);
        ISimplePacketHandler ph = packet.getPacketHandler();
        if (ph != null && ph != packetHandler){
            throw new IllegalArgumentException();
        }
        packetToId.put(packet.getClass(), b);
        registerPacketHandler(packetHandler);
    }

    @Override
    public IPacketDispatcher getPacketDispatcher() {
        return packetDispatcher;
    }

    @Override
    public IMessage onMessage(PacketSimplePacket message, MessageContext ctx) {
        Preconditions.checkNotNull(idToHandler.get(message.i)).onPacket(new ElecByteBufImpl(message.data), NetworkManager.INSTANCE.wrapMessageContext(ctx), this);
        return null;
    }

    @SuppressWarnings("all")
    public static class PacketSimplePacket implements IMessage {


        public PacketSimplePacket(){
        }

        PacketSimplePacket(int i, ByteBuf buf){
            this.i = (byte) i;
            this.data = buf;
        }

        byte i;
        ByteBuf data;

        @Override
        public void fromBytes(ByteBuf buf) {
            this.i = buf.readByte();
            this.data = Unpooled.buffer(buf.readableBytes());
            buf.readBytes(this.data);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeByte(i);
            buf.writeBytes(data);
        }

    }

}
