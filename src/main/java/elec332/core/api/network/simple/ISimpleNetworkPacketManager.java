package elec332.core.api.network.simple;

import elec332.core.api.network.IPacketDispatcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

/**
 * Created by Elec332 on 23-10-2016.
 *
 * Packet manager for simple packets
 */
public interface ISimpleNetworkPacketManager {

    /**
     * Returns the name of this channel.
     *
     * @return The name of this channel
     */
    public String getChannelName();


    /**
     * Send this message to everyone.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message The message to send
     */
    public void sendToAll(ISimplePacket message);

    /**
     * Send this message to everyone.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     */
    public void sendToAll(ISimplePacket message, ISimplePacketHandler packetHandler);

    /**
     * Send the data provided in the provided {@link ByteBuf} to everyone.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     */
    public void sendToAll(ByteBuf data, ISimplePacketHandler packetHandler);


    /**
     * Send this message to the specified player.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message The message to send
     * @param player  The player to send it to
     */
    public void sendTo(ISimplePacket message, EntityPlayerMP player);

    /**
     * Send this message to the specified player.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     * @param player        The player to send it to
     */
    public void sendTo(ISimplePacket message, ISimplePacketHandler packetHandler, EntityPlayerMP player);

    /**
     * Send the data provided in the provided {@link ByteBuf} to the specified player.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     * @param player        The player to send it to
     */
    public void sendTo(ByteBuf data, ISimplePacketHandler packetHandler, EntityPlayerMP player);



    /**
     * Send this message to everyone within a certain range of a point defined in the packet.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message The message to send
     * @param point   The {@link NetworkRegistry.TargetPoint} around which to send
     */
    public void sendToAllAround(ISimplePacket message, NetworkRegistry.TargetPoint point);

    /**
     * Send this message to everyone within a certain range of a point defined in the packet.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     * @param point         The {@link NetworkRegistry.TargetPoint} around which to send
     */
    public void sendToAllAround(ISimplePacket message, ISimplePacketHandler packetHandler, NetworkRegistry.TargetPoint point);

    /**
     * Send the data provided in the provided {@link ByteBuf} to everyone within a certain range of a point defined in the packet.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     * @param point         The {@link NetworkRegistry.TargetPoint} around which to send
     */
    public void sendToAllAround(ByteBuf data, ISimplePacketHandler packetHandler, NetworkRegistry.TargetPoint point);


    /**
     * Send this message to everyone within the supplied dimension.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message     The message to send
     * @param dimensionId The dimension id to target
     */
    public void sendToDimension(ISimplePacket message, int dimensionId);

    /**
     * Send this message to everyone within the supplied dimension.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message       The message to send
     * @param packetHandler The packet-handler the client has to use
     * @param dimensionId   The dimension id to target
     */
    public void sendToDimension(ISimplePacket message, ISimplePacketHandler packetHandler, int dimensionId);

    /**
     * Send the data provided in the provided {@link ByteBuf} to everyone within the supplied dimension.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     * @param dimensionId   The dimension id to target
     */
    public void sendToDimension(ByteBuf data, ISimplePacketHandler packetHandler, int dimensionId);


    /**
     * Send this message to the server.
     * Uses the {@link ISimplePacketHandler} defined by {@link ISimplePacket#getPacketHandler()}.
     *
     * @param message The message to send
     */
    public void sendToServer(ISimplePacket message);

    /**
     * Send this message to the server.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param message The message to send
     * @param packetHandler The packet-handler the client has to use
     */
    public void sendToServer(ISimplePacket message, ISimplePacketHandler packetHandler);

    /**
     * Send the data provided in the provided {@link ByteBuf} to the server.
     * Uses the {@link ISimplePacketHandler} defined by {@param packetHandler}.
     *
     * @param data          The data to send
     * @param packetHandler The packet-handler the client has to use
     */
    public void sendToServer(ByteBuf data, ISimplePacketHandler packetHandler);


    /////Packet registering

    public void registerPacket(Class<? extends ISimplePacket> packetType);

    public void registerPacket(ISimplePacket packet);

    public void registerPacketHandler(ISimplePacketHandler packetHandler);

    public void registerPacket(Class<? extends ISimplePacket> packetType, ISimplePacketHandler packetHandler);

    public void registerPacket(ISimplePacket packet, ISimplePacketHandler packetHandler);

    /**
     * Used to get the {@link IPacketDispatcher} used by this {@link ISimpleNetworkPacketManager}
     *
     * @return The {@link IPacketDispatcher} used by this {@link ISimpleNetworkPacketManager}}
     */
    public IPacketDispatcher getPacketDispatcher();

}
