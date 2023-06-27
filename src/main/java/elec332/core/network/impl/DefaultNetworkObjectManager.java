package elec332.core.network.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import elec332.core.ElecCore;
import elec332.core.api.network.*;
import elec332.core.api.network.object.*;
import elec332.core.api.util.IEntityFilter;
import elec332.core.util.FMLHelper;
import elec332.core.world.WorldHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 23-10-2016.
 */
class DefaultNetworkObjectManager implements INetworkObjectManager, BiConsumer<DefaultNetworkObjectManager.PacketNetworkObject, Supplier<IExtendedMessageContext>> {

    DefaultNetworkObjectManager(INetworkHandler handler) {
        this.packetDispatcher = handler;
        handler.registerPacket(this, PacketNetworkObject.class);
        this.packetStuff = Lists.newArrayList();
        this.i = 0;
    }

    private final IPacketDispatcher packetDispatcher;
    private final List<NOH<?>> packetStuff;
    private byte i;

    @Override
    @SuppressWarnings("unchecked")
    public <N extends INetworkObjectReceiver<S>, S extends INetworkObjectSender<S>> INetworkObjectHandler<?> registerNetworkObject(N networkObject) {
        if (networkObject instanceof INetworkObjectSender) {
            return registerNetworkObject(networkObject, (S) networkObject);
        }
        if (!FMLHelper.isInModInitialisation()) {
            throw new IllegalStateException();
        }
        NOH<S> ret = new NOH<>(i, null, networkObject);
        packetStuff.add(ret);
        i++;
        networkObject.setNetworkObjectHandler(ret);
        return ret;
    }

    @Override
    public <R extends INetworkObjectReceiver<S>, S extends INetworkObjectSender<S>> INetworkObjectHandler<S> registerNetworkObject(@Nullable R networkObjectR, @Nullable S networkObjectS) {
        if (!FMLHelper.isInModInitialisation() || (networkObjectS == null && networkObjectR == null)) {
            throw new IllegalStateException();
        }
        NOH<S> ret = new NOH<>(i, networkObjectS, networkObjectR);
        packetStuff.add(ret);
        i++;

        if (networkObjectS != networkObjectR) {
            if (networkObjectR != null) {
                networkObjectR.setNetworkObjectHandler(ret);
            }
            if (networkObjectS != null) {
                networkObjectS.setNetworkObjectHandler(ret);
            }
        } else {
            networkObjectR.setNetworkObjectHandler(ret);
        }

        return ret;
    }

    @Override
    public <N extends INetworkObject<N>> INetworkObjectHandler<N> registerSpecialNetworkObject(N networkObject) {
        if (!FMLHelper.isInModInitialisation()) {
            throw new IllegalStateException();
        }
        NOH<N> ret = new NOH<>(i, networkObject, networkObject);
        packetStuff.add(ret);
        i++;
        networkObject.setNetworkObjectHandler(ret);
        return ret;
    }

    @Override
    public void accept(PacketNetworkObject message, Supplier<IExtendedMessageContext> extendedMessageContext) {
        ElecCore.tickHandler.registerCall(() -> {
            Iterator<NOH<?>> iterator = packetStuff.iterator();
            NOH<?> noh = iterator.next();
            while (!noh.handle(message)) {
                noh = iterator.next();
            }
        }, extendedMessageContext.get().getReceptionSide());
    }

    private class NOH<T extends INetworkObjectSender<T>> implements INetworkObjectHandler<T>, DefaultByteBufFactory {

        private NOH(byte i, @Nullable T obj, @Nullable INetworkObjectReceiver<T> receiver) {
            this.b = i;
            this.obj = obj;
            this.receiver = receiver;
        }

        private final byte b;
        private final T obj;
        private final INetworkObjectReceiver<T> receiver;

        @Override
        public void sendToAll(int id) {
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendToAll(id, buf);
        }

        @Override
        public void sendTo(int id, IEntityFilter<ServerPlayerEntity> playerFilter, MinecraftServer server) {
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendTo(id, buf, playerFilter, server);
        }

        @Override
        public void sendTo(int id, List<ServerPlayerEntity> players) {
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendTo(id, buf, players);
        }

        @Override
        public void sendTo(int id, ServerPlayerEntity player) {
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendTo(id, buf, player);
        }

        @Override
        public void sendToAllAround(int id, IPacketDispatcher.TargetPoint point) {
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendToAllAround(id, buf, point);
        }

        @Override
        public void sendToDimension(int id, ResourceLocation dimensionId) {
            sendToDimension(id, WorldHelper.getWorldKey(dimensionId));
        }

        @Override
        public void sendToDimension(int id, RegistryKey<World> dimensionId) {
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendToDimension(id, buf, dimensionId);
        }

        @Override
        public void sendToServer(int id) {
            ByteBuf buf = Unpooled.buffer();
            if (getNetworkObjectSender() != null) {
                getNetworkObjectSender().writePacket(id, new ElecByteBufImpl(buf));
            }
            sendToServer(id, buf);
        }

        @Override
        public void sendToAll(int id, CompoundNBT data) {
            sendToAll(id, fromTag(data));
        }

        @Override
        public void sendTo(int id, CompoundNBT data, IEntityFilter<ServerPlayerEntity> playerFilter, MinecraftServer server) {
            sendTo(id, fromTag(data), playerFilter, server);
        }

        @Override
        public void sendTo(int id, CompoundNBT data, List<ServerPlayerEntity> players) {
            sendTo(id, fromTag(data), players);
        }

        @Override
        public void sendTo(int id, CompoundNBT data, ServerPlayerEntity player) {
            sendTo(id, fromTag(data), player);
        }

        @Override
        public void sendToAllAround(int id, CompoundNBT data, IPacketDispatcher.TargetPoint point) {
            sendToAllAround(id, fromTag(data), point);
        }

        @Override
        public void sendToDimension(int id, CompoundNBT data, RegistryKey<World> dimensionId) {
            sendToDimension(id, fromTag(data), dimensionId);
        }

        @Override
        public void sendToDimension(int id, CompoundNBT data, ResourceLocation dimensionId) {
            sendToDimension(id, fromTag(data), dimensionId);
        }

        @Override
        public void sendToServer(int id, CompoundNBT data) {
            sendToServer(id, fromTag(data));
        }

        @Override
        public void sendToAll(int id, ByteBuf data) {
            packetDispatcher.sendToAll(new PacketNetworkObject(b, (byte) id, data));
        }

        @Override
        public void sendTo(int id, ByteBuf data, ServerPlayerEntity player) {
            packetDispatcher.sendTo(new PacketNetworkObject(b, (byte) id, data), player);
        }

        @Override
        public void sendToAllAround(int id, ByteBuf data, IPacketDispatcher.TargetPoint point) {
            packetDispatcher.sendToAllAround(new PacketNetworkObject(b, (byte) id, data), point);
        }

        @Override
        public void sendToDimension(int id, ByteBuf data, RegistryKey<World> dimensionId) {
            packetDispatcher.sendToDimension(new PacketNetworkObject(b, (byte) id, data), dimensionId);
        }

        @Override
        public void sendToDimension(int id, ByteBuf data, ResourceLocation dimensionId) {
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

        private ByteBuf fromTag(CompoundNBT tag) {
            ElecByteBuf buf = new ElecByteBufImpl(Unpooled.buffer());
            buf.writeCompoundNBTToBuffer(tag);
            return buf;
        }

        private boolean handle(PacketNetworkObject packet) {
            if (packet.i == b) {
                try {
                    Preconditions.checkNotNull(receiver).onPacket(packet.i2, new ElecByteBufImpl(packet.data));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            return false;
        }

    }

    public static class PacketNetworkObject implements IMessage {

        @SuppressWarnings("unused")
        public PacketNetworkObject() {
        }

        PacketNetworkObject(byte b, byte b2, ByteBuf buf) {
            this.i = b;
            this.i2 = b2;
            this.data = buf;
        }

        private byte i, i2;
        private ByteBuf data;

        @Override
        public void fromBytes(PacketBuffer buf) {
            this.i = buf.readByte();
            this.i2 = buf.readByte();
            this.data = new PacketBuffer(Unpooled.wrappedBuffer(buf.readByteArray()));
        }

        @Override
        public void toBytes(PacketBuffer buf) {
            buf.writeByte(i);
            buf.writeByte(i2);
            buf.writeVarInt(data.readableBytes());
            buf.writeBytes(data);
        }

    }


}
