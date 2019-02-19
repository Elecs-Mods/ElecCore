package elec332.core.network.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.api.network.IMessage;
import elec332.core.api.network.IPacketDispatcher;
import elec332.core.api.network.IPacketRegistry;
import elec332.core.api.network.object.*;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.api.network.simple.ISimplePacket;
import elec332.core.api.network.simple.ISimplePacketHandler;
import elec332.core.network.IElecNetworkHandler;
import elec332.core.util.FieldPointer;
import elec332.core.util.ServerHelper;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkInstance;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.IndexedMessageCodec;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 23-2-2015.
 */
class DefaultNetworkHandler implements IElecNetworkHandler, DefaultByteBufFactory {

    DefaultNetworkHandler(ResourceLocation channelName, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        this(NetworkRegistry.newSimpleChannel(channelName, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions), channelName);
    }

    DefaultNetworkHandler(ResourceLocation channelName) {
        this(NetworkRegistry.newSimpleChannel(channelName, () -> "0", s -> true, s -> true), channelName);
    }

    DefaultNetworkHandler(SimpleChannel networkWrapper) {
        this(networkWrapper, nameGetter.get(networkWrapper).getChannelName());
    }

    private DefaultNetworkHandler(SimpleChannel networkWrapper, ResourceLocation name) {
        this.networkWrapper = networkWrapper;
        this.channelName = name;
        this.i = 0;
        this.networkObjectManager = new DefaultNetworkObjectManager(this);
        this.simpleNetworkPacketManager = new DefaultSimpleNetworkHandler(this, channelName);
        this.packetManagers = Maps.newHashMap();
    }

    private final SimpleChannel networkWrapper;
    private final INetworkObjectManager networkObjectManager;
    private final ISimpleNetworkPacketManager simpleNetworkPacketManager;
    private final Map<ResourceLocation, ISimpleNetworkPacketManager> packetManagers;
    private final ResourceLocation channelName;
    private int i;

    @Nonnull
    ISimpleNetworkPacketManager getSimpleNetworkManager(ResourceLocation s) {
        if (s.equals(channelName)) {
            return simpleNetworkPacketManager;
        }
        ISimpleNetworkPacketManager ret = packetManagers.get(s);
        if (ret == null) {
            packetManagers.put(s, ret = new DefaultSimpleNetworkHandler(this, s));
        }
        return Preconditions.checkNotNull(ret);
    }

    @Override
    public <M extends IMessage> void registerPacket(BiConsumer<M, Supplier<IExtendedMessageContext>> messageHandler, Class<M> messageType) {
        try {
            messageType.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        networkWrapper.registerMessage(getNextIndex(),
                messageType,
                IMessage::toBytes,
                packetBuffer -> {
                    try {
                        M m = messageType.newInstance();
                        m.fromBytes(packetBuffer);
                        return m;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (m, contextSupplier) -> {
                    NetworkEvent.Context context = contextSupplier.get();
                    messageHandler.accept(m, () -> NetworkManager.INSTANCE.wrapMessageContext(context));
                    context.setPacketHandled(true);
                }
        );
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
    public ResourceLocation getChannelName() {
        return channelName;
    }

    @Override
    public void sendToAllAround(IMessage message, TargetPoint point) {
        sendTo(message, ServerHelper.getAllPlayersInDimension(point.dimension).stream()
                .filter(player -> {
                    double d4 = point.x - player.posX;
                    double d5 = point.y - player.posY;
                    double d6 = point.z - player.posZ;
                    return d4 * d4 + d5 * d5 + d6 * d6 < point.range * point.range;
                }));
    }

    @Override
    public void sendToDimension(IMessage message, DimensionType dimensionId) {
        sendTo(message, ServerHelper.getAllPlayersInDimension(dimensionId));
    }

    @Override
    public void sendToAll(IMessage message) {
        sendTo(message, ServerHelper.getOnlinePlayers());
    }

    @Override
    public void sendTo(IMessage message, EntityPlayerMP player) {
        networkWrapper.sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void sendToServer(IMessage message) {
        networkWrapper.sendToServer(message);
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
    public void sendToAllAround(ISimplePacket message, TargetPoint point) {
        simpleNetworkPacketManager.sendToAllAround(message, point);
    }

    @Override
    public void sendToDimension(ISimplePacket message, DimensionType dimensionId) {
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
    public void sendToAllAround(ISimplePacket message, ISimplePacketHandler packetHandler, TargetPoint point) {
        simpleNetworkPacketManager.sendToAllAround(message, packetHandler, point);
    }

    @Override
    public void sendToDimension(ISimplePacket message, ISimplePacketHandler packetHandler, DimensionType dimensionId) {
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
    public void sendToAllAround(ByteBuf data, ISimplePacketHandler packetHandler, TargetPoint point) {
        simpleNetworkPacketManager.sendToAllAround(data, packetHandler, point);
    }

    @Override
    public void sendToDimension(ByteBuf data, ISimplePacketHandler packetHandler, DimensionType dimensionId) {
        simpleNetworkPacketManager.sendToDimension(data, packetHandler, dimensionId);
    }

    @Override
    public void sendToServer(ByteBuf data, ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.sendToServer(data, packetHandler);
    }

    @Override
    public void registerSimplePacket(Class<? extends ISimplePacket> packetType) {
        simpleNetworkPacketManager.registerSimplePacket(packetType);
    }

    @Override
    public void registerSimplePacket(ISimplePacket packet) {
        simpleNetworkPacketManager.registerSimplePacket(packet);
    }

    @Override
    public void registerSimplePacketHandler(ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.registerSimplePacketHandler(packetHandler);
    }

    @Override
    public void registerSimplePacket(Class<? extends ISimplePacket> packetType, ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.registerSimplePacket(packetType, packetHandler);
    }

    @Override
    public void registerSimplePacket(ISimplePacket packet, ISimplePacketHandler packetHandler) {
        simpleNetworkPacketManager.registerSimplePacket(packet, packetHandler);
    }

    @Override
    public IPacketDispatcher getPacketDispatcher() {
        return this;
    }

    @SuppressWarnings("unchecked")
    private int getNextIndex() {
        while (messageIndexUsed.test(networkWrapper, (short) i)) {
            i++;
        }
        int ret = i;
        i++;
        return ret;
    }

    private static final BiPredicate<SimpleChannel, Short> messageIndexUsed;
    private static final FieldPointer<SimpleChannel, IndexedMessageCodec> indexer;
    private static final FieldPointer<IndexedMessageCodec, Short2ObjectArrayMap> indexer2;
    private static final FieldPointer<SimpleChannel, NetworkInstance> nameGetter;

    static {
        indexer = new FieldPointer<>(SimpleChannel.class, "indexedCodec");
        indexer2 = new FieldPointer<>(IndexedMessageCodec.class, "indicies");
        messageIndexUsed = (c, i) -> indexer2.get(indexer.get(c)).containsKey((short) i);
        nameGetter = new FieldPointer<>(SimpleChannel.class, "instance");
    }

}
