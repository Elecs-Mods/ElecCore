package elec332.core.network.impl;

import com.google.common.collect.Lists;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.INetworkHandler;
import elec332.core.api.network.IPacketDispatcher;
import elec332.core.api.network.object.*;
import elec332.core.api.util.IEntityFilter;
import elec332.core.main.ElecCore;
import elec332.core.util.FMLUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Elec332 on 23-10-2016.
 */
@SuppressWarnings("all")
class DefaultNetworkObjectManager implements INetworkObjectManager, IMessageHandler<DefaultNetworkObjectManager.PacketNetworkObject, IMessage> {

    DefaultNetworkObjectManager(INetworkHandler handler){
        this.packetDispatcher = handler;
        handler.registerPacket(this, PacketNetworkObject.class, Side.CLIENT);
        handler.registerPacket(this, PacketNetworkObject.class, Side.SERVER);
        this.packetStuff = Lists.newArrayList();
        this.i = 0;
    }

    private IPacketDispatcher packetDispatcher;
    private final List<NOH> packetStuff;
    private byte i;

    @Override
    public <N extends INetworkObjectReceiver> INetworkObjectHandler<?> registerNetworkObject(N networkObject){
        if (networkObject instanceof INetworkObjectSender){
            return registerNetworkObject(networkObject, (INetworkObjectSender) networkObject);
        }
        if (!FMLUtil.isInModInitialisation()){
            throw new IllegalStateException();
        }
        NOH<?> ret = new NOH(i, null, networkObject);
        packetStuff.add(ret);
        i++;
        networkObject.setNetworkObjectHandler(ret);
        return ret;
    }

    @Override
    public <N extends INetworkObjectReceiver, S extends INetworkObjectSender> INetworkObjectHandler<S> registerNetworkObject(@Nullable N networkObjectR, @Nullable S networkObjectS) {
        if (!FMLUtil.isInModInitialisation() || (networkObjectS == null && networkObjectR == null)){
            throw new IllegalStateException();
        }
        NOH<S> ret = new NOH<S>(i, networkObjectS, networkObjectR);
        packetStuff.add(ret);
        i++;

        if (networkObjectS != networkObjectR){
            if (networkObjectR != null) {
                networkObjectR.setNetworkObjectHandler(ret);
            }
            if (networkObjectS != null){
                networkObjectS.setNetworkObjectHandler(ret);
            }
        } else {
            networkObjectR.setNetworkObjectHandler(ret);
        }

        return ret;
    }

    @Override
    public <N extends INetworkObject> INetworkObjectHandler<N> registerSpecialNetworkObject(N networkObject) {
        if (!FMLUtil.isInModInitialisation()){
            throw new IllegalStateException();
        }
        NOH<N> ret = new NOH<N>(i, networkObject, networkObject);
        packetStuff.add(ret);
        i++;
        networkObject.setNetworkObjectHandler(ret);
        return ret;
    }

    @Override
    public IMessage onMessage(final PacketNetworkObject message, MessageContext ctx) {
        ElecCore.tickHandler.registerCall(new Runnable() {

            @Override
            public void run() {
                Iterator<NOH> iterator = packetStuff.iterator();
                NOH noh = iterator.next();
                while (!noh.handle(message)){
                    noh = iterator.next();
                }
            }

        }, ctx.side);
        return null;
    }

    private class NOH<T extends INetworkObjectSender> implements INetworkObjectHandler<T>, DefaultByteBufFactory {

        private NOH(byte i, @Nullable T obj, @Nullable INetworkObjectReceiver receiver){
            this.b = i;
            this.obj = obj;
            this.receiver = receiver;
        }

        private byte b;
        private T obj;
        private INetworkObjectReceiver receiver;

        @Override
        public void sendToAll(int id){
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendToAll(id, buf);
        }

        @Override
        public void sendTo(int id, IEntityFilter<EntityPlayerMP> playerFilter, MinecraftServer server){
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendTo(id, buf, playerFilter, server);
        }

        @Override
        public void sendTo(int id, List<EntityPlayerMP> players){
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendTo(id, buf, players);
        }

        @Override
        public void sendTo(int id, EntityPlayerMP player){
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendTo(id, buf, player);
        }

        @Override
        public void sendToAllAround(int id, NetworkRegistry.TargetPoint point){
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendToAllAround(id, buf, point);
        }

        @Override
        public void sendToDimension(int id, int dimensionId){
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendToDimension(id, buf, dimensionId);
        }

        @Override
        public void sendToServer(int id){
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendToServer(id, buf);
        }

        @Override
        public void sendToAll(int id, NBTTagCompound data){
            sendToAll(id, fromTag(data));
        }

        @Override
        public void sendTo(int id, NBTTagCompound data, IEntityFilter<EntityPlayerMP> playerFilter, MinecraftServer server){
            sendTo(id, fromTag(data), playerFilter, server);
        }

        @Override
        public void sendTo(int id, NBTTagCompound data, List<EntityPlayerMP> players){
            sendTo(id, fromTag(data), players);
        }

        @Override
        public void sendTo(int id, NBTTagCompound data, EntityPlayerMP player){
            sendTo(id, fromTag(data), player);
        }

        @Override
        public void sendToAllAround(int id, NBTTagCompound data, NetworkRegistry.TargetPoint point){
            sendToAllAround(id, fromTag(data), point);
        }

        @Override
        public void sendToDimension(int id, NBTTagCompound data, int dimensionId){
            sendToDimension(id, fromTag(data), dimensionId);
        }

        @Override
        public void sendToServer(int id, NBTTagCompound data){
            sendToServer(id, fromTag(data));
        }

        @Override
        public void sendToAll(int id, ByteBuf data) {
            packetDispatcher.sendToAll(new PacketNetworkObject(b, (byte) id, data));
        }

        @Override
        public void sendTo(int id, ByteBuf data, EntityPlayerMP player) {
            packetDispatcher.sendTo(new PacketNetworkObject(b, (byte) id, data), player);
        }

        @Override
        public void sendToAllAround(int id, ByteBuf data, NetworkRegistry.TargetPoint point) {
            packetDispatcher.sendToAllAround(new PacketNetworkObject(b, (byte) id, data), point);
        }

        @Override
        public void sendToDimension(int id, ByteBuf data, int dimensionId) {
            packetDispatcher.sendToDimension(new PacketNetworkObject(b, (byte) id, data), dimensionId);
        }

        @Override
        public void sendToServer(int id, ByteBuf data) {
            packetDispatcher.sendToServer(new PacketNetworkObject(b, (byte) id, data));
        }

        @Override
        @Nullable
        public T getNetworkObjectSender() {
            return obj;
        }

        private ByteBuf fromTag(NBTTagCompound tag){
            ElecByteBuf buf = new ElecByteBufImpl(Unpooled.buffer());
            buf.writeNBTTagCompoundToBuffer(tag);
            return buf;
        }

        private boolean handle(PacketNetworkObject packet){
            if (packet.i == b){
                try {
                    receiver.onPacket(packet.i2, new ElecByteBufImpl(packet.data));
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                return true;
            }
            return false;
        }

    }

    public static class PacketNetworkObject implements IMessage {

        public PacketNetworkObject(){
        }

        PacketNetworkObject(byte b, byte b2, ByteBuf buf){
            this.i = b;
            this.i2 = b2;
            this.data = buf;
        }

        byte i, i2;
        ByteBuf data;

        @Override
        public void fromBytes(ByteBuf buf) {
            this.i = buf.readByte();
            this.i2 = buf.readByte();
            this.data = buf.readBytes(Unpooled.buffer(buf.readableBytes()));
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeByte(i);
            buf.writeByte(i2);
            buf.writeBytes(data);
        }

    }


}
