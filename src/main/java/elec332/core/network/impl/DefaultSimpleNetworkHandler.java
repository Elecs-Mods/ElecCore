package elec332.core.network.impl;

import com.google.common.base.Preconditions;
import elec332.core.api.network.*;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.api.network.simple.ISimplePacket;
import elec332.core.api.network.simple.ISimplePacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 23-10-2016.
 */
class DefaultSimpleNetworkHandler implements ISimpleNetworkPacketManager, BiConsumer<DefaultSimpleNetworkHandler.PacketSimplePacket, Supplier<IExtendedMessageContext>> {

    @SuppressWarnings("all")
    DefaultSimpleNetworkHandler(INetworkHandler networkHandler, ResourceLocation s) {
        this.idToHandler = new Int2ObjectOpenHashMap<>();
        this.handlerToId = new Object2IntOpenHashMap<>();
        this.packetToId = new Object2IntOpenHashMap<>();
        this.b = 0;
        networkHandler.registerPacket(this, PacketSimplePacket.class);
        this.packetDispatcher = networkHandler;
        this.s = s;
    }

    private final IPacketDispatcher packetDispatcher;
    private final ResourceLocation s;
    private Int2ObjectMap<ISimplePacketHandler> idToHandler;
    private Object2IntMap<ISimplePacketHandler> handlerToId;
    private Object2IntMap<Class<? extends ISimplePacket>> packetToId;
    private byte b;

    @Override
    public ResourceLocation getChannelName() {
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
    public void sendToAllAround(ISimplePacket message, IPacketDispatcher.TargetPoint point) {
        packetDispatcher.sendToAllAround(from(message), point);
    }

    @Override
    public void sendToDimension(ISimplePacket message, DimensionType dimensionId) {
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
    public void sendToAllAround(ISimplePacket message, ISimplePacketHandler packetHandler, IPacketDispatcher.TargetPoint point) {
        packetDispatcher.sendToAllAround(from(message, packetHandler), point);
    }

    @Override
    public void sendToDimension(ISimplePacket message, ISimplePacketHandler packetHandler, DimensionType dimensionId) {
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
    public void sendToAllAround(ByteBuf data, ISimplePacketHandler packetHandler, IPacketDispatcher.TargetPoint point) {
        packetDispatcher.sendToAllAround(from(data, packetHandler), point);
    }

    @Override
    public void sendToDimension(ByteBuf data, ISimplePacketHandler packetHandler, DimensionType dimensionId) {
        packetDispatcher.sendToDimension(from(data, packetHandler), dimensionId);
    }

    @Override
    public void sendToServer(ByteBuf data, ISimplePacketHandler packetHandler) {
        packetDispatcher.sendToServer(from(data, packetHandler));
    }

    private PacketSimplePacket from(ISimplePacket message) {
        return new PacketSimplePacket(packetToId.get(message.getClass()), fetchData(message));
    }

    private PacketSimplePacket from(ISimplePacket message, ISimplePacketHandler handler) {
        return new PacketSimplePacket(handlerToId.get(handler), fetchData(message));
    }

    private PacketSimplePacket from(ByteBuf data, ISimplePacketHandler handler) {
        return new PacketSimplePacket(handlerToId.get(handler), data);
    }

    private ByteBuf fetchData(ISimplePacket packet) {
        ElecByteBuf ret = new ElecByteBufImpl(UnpooledByteBufAllocator.DEFAULT.ioBuffer());
        packet.toBytes(ret);
        return ret;
    }

    @Override
    public void registerSimplePacket(Class<? extends ISimplePacket> packet) {
        try {
            registerSimplePacket(packet.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerSimplePacket(ISimplePacket packet) {
        ISimplePacketHandler handler = packet.getPacketHandler();
        if (handler == null) {
            throw new UnsupportedOperationException();
        }
        registerSimplePacket(packet, handler);
    }

    @Override
    public void registerSimplePacketHandler(ISimplePacketHandler packetHandler) {
        Preconditions.checkNotNull(packetHandler);
        idToHandler.put(b, packetHandler);
        handlerToId.put(packetHandler, b);
        b++;
    }

    @Override
    public void registerSimplePacket(Class<? extends ISimplePacket> packet, ISimplePacketHandler packetHandler) {
        try {
            registerSimplePacket(packet.newInstance(), packetHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerSimplePacket(ISimplePacket packet, ISimplePacketHandler packetHandler) {
        Preconditions.checkNotNull(packet);
        Preconditions.checkNotNull(packetHandler);
        ISimplePacketHandler ph = packet.getPacketHandler();
        if (ph != null && ph != packetHandler) {
            throw new IllegalArgumentException();
        }
        packetToId.put(packet.getClass(), b);
        registerSimplePacketHandler(packetHandler);
    }

    @Override
    public IPacketDispatcher getPacketDispatcher() {
        return packetDispatcher;
    }

    @Override
    public void accept(PacketSimplePacket message, Supplier<IExtendedMessageContext> extendedMessageContext) {
        Preconditions.checkNotNull(idToHandler.get(message.i)).onPacket(new ElecByteBufImpl(message.data), extendedMessageContext.get(), this);
    }

    @SuppressWarnings("all")
    public static class PacketSimplePacket implements IMessage {


        public PacketSimplePacket() {
        }

        PacketSimplePacket(int i, ByteBuf buf) {
            this.i = (byte) i;
            this.data = buf;
        }

        byte i;
        ByteBuf data;

        @Override
        public void fromBytes(PacketBuffer buf) {
            this.i = buf.readByte();
            this.data = Unpooled.buffer(buf.readableBytes());
            buf.readBytes(this.data);
        }

        @Override
        public void toBytes(PacketBuffer buf) {
            buf.writeByte(i);
            buf.writeBytes(data);
        }

    }

}
