package elec332.core.api.network.simple;

import elec332.core.api.network.IPacketDispatcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 23-10-2016.
 * <p>
 * Packet manager for simple packets
 */
public interface ISimpleNetworkPacketManager {

    /**
     * Returns the name of this channel.
     *
     * @return The name of this channel
     */
    ResourceLocation getChannelName();


    /**
     * Send this message to everyone.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message The message to send
     */
    void sendToAll(ISimplePacket message);

    /**
     * Send this message to everyone.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     */
    void sendToAll(ISimplePacket message, ISimplePacketHandler packetHandler);

    /**
     * Send the data provided in the provided {@link ByteBuf} to everyone.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     */
    void sendToAll(ByteBuf data, ISimplePacketHandler packetHandler);


    /**
     * Send this message to the specified player.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message The message to send
     * @param player  The player to send it to
     */
    void sendTo(ISimplePacket message, ServerPlayerEntity player);

    /**
     * Send this message to the specified player.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     * @param player        The player to send it to
     */
    void sendTo(ISimplePacket message, ISimplePacketHandler packetHandler, ServerPlayerEntity player);

    /**
     * Send the data provided in the provided {@link ByteBuf} to the specified player.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     * @param player        The player to send it to
     */
    void sendTo(ByteBuf data, ISimplePacketHandler packetHandler, ServerPlayerEntity player);


    /**
     * Send this message to everyone within a certain range of a point defined in the packet.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message The message to send
     * @param point   The {@link IPacketDispatcher.TargetPoint} around which to send
     */
    void sendToAllAround(ISimplePacket message, IPacketDispatcher.TargetPoint point);

    /**
     * Send this message to everyone within a certain range of a point defined in the packet.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     * @param point         The {@link IPacketDispatcher.TargetPoint} around which to send
     */
    void sendToAllAround(ISimplePacket message, ISimplePacketHandler packetHandler, IPacketDispatcher.TargetPoint point);

    /**
     * Send the data provided in the provided {@link ByteBuf} to everyone within a certain range of a point defined in the packet.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     * @param point         The {@link IPacketDispatcher.TargetPoint} around which to send
     */
    void sendToAllAround(ByteBuf data, ISimplePacketHandler packetHandler, IPacketDispatcher.TargetPoint point);


    /**
     * Send this message to everyone within the supplied dimension.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message       The message to send
     * @param dimensionName The dimension to target
     */
    void sendToDimension(ISimplePacket message, ResourceLocation dimensionName);

    /**
     * Send this message to everyone within the supplied dimension.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message     The message to send
     * @param dimensionId The dimension id to target
     */
    void sendToDimension(ISimplePacket message, RegistryKey<World> dimensionId);

    /**
     * Send this message to everyone within the supplied dimension.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     * @param dimensionName The dimension to target
     */
    void sendToDimension(ISimplePacket message, ISimplePacketHandler packetHandler, ResourceLocation dimensionName);

    /**
     * Send this message to everyone within the supplied dimension.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     * @param dimensionId   The dimension id to target
     */
    void sendToDimension(ISimplePacket message, ISimplePacketHandler packetHandler, RegistryKey<World> dimensionId);

    /**
     * Send the data provided in the provided {@link ByteBuf} to everyone within the supplied dimension.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     * @param dimensionName The dimension to target
     */
    void sendToDimension(ByteBuf data, ISimplePacketHandler packetHandler, ResourceLocation dimensionName);

    /**
     * Send the data provided in the provided {@link ByteBuf} to everyone within the supplied dimension.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     * @param dimensionId   The dimension id to target
     */
    void sendToDimension(ByteBuf data, ISimplePacketHandler packetHandler, RegistryKey<World> dimensionId);


    /**
     * Send this message to the server.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message The message to send
     */
    void sendToServer(ISimplePacket message);

    /**
     * Send this message to the server.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     */
    void sendToServer(ISimplePacket message, ISimplePacketHandler packetHandler);

    /**
     * Send the data provided in the provided {@link ByteBuf} to the server.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     */
    void sendToServer(ByteBuf data, ISimplePacketHandler packetHandler);


    /////Packet registering

    void registerSimplePacket(Class<? extends ISimplePacket> packetType);

    void registerSimplePacket(ISimplePacket packet);

    void registerSimplePacketHandler(ISimplePacketHandler packetHandler);

    void registerSimplePacket(Class<? extends ISimplePacket> packetType, ISimplePacketHandler packetHandler);

    void registerSimplePacket(ISimplePacket packet, ISimplePacketHandler packetHandler);

    /**
     * Used to get the {@link IPacketDispatcher} used by this {@link ISimpleNetworkPacketManager}
     *
     * @return The {@link IPacketDispatcher} used by this {@link ISimpleNetworkPacketManager}}
     */
    IPacketDispatcher getPacketDispatcher();

}
