package elec332.core.api.network.simple;

import elec332.core.api.network.IPacketDispatcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface ISimpleNetworkPacketManager {

    public String getChannelName();

    public void sendToAll(ISimplePacket message);

    public void sendTo(ISimplePacket message, EntityPlayerMP player);

    public void sendToAllAround(ISimplePacket message, NetworkRegistry.TargetPoint point);

    public void sendToDimension(ISimplePacket message, int dimensionId);

    public void sendToServer(ISimplePacket message);

    public void sendToAll(ISimplePacket message, ISimplePacketHandler packetHandler);

    public void sendTo(ISimplePacket message, ISimplePacketHandler packetHandler, EntityPlayerMP player);

    public void sendToAllAround(ISimplePacket message, ISimplePacketHandler packetHandler, NetworkRegistry.TargetPoint point);

    public void sendToDimension(ISimplePacket message, ISimplePacketHandler packetHandler, int dimensionId);

    public void sendToServer(ISimplePacket message, ISimplePacketHandler packetHandler);

    public void sendToAll(ByteBuf data, ISimplePacketHandler packetHandler);

    public void sendTo(ByteBuf data, ISimplePacketHandler packetHandler, EntityPlayerMP player);

    public void sendToAllAround(ByteBuf data, ISimplePacketHandler packetHandler, NetworkRegistry.TargetPoint point);

    public void sendToDimension(ByteBuf data, ISimplePacketHandler packetHandler, int dimensionId);

    public void sendToServer(ByteBuf data, ISimplePacketHandler packetHandler);

    public void registerPacket(Class<ISimplePacket> packetType);

    public void registerPacket(ISimplePacket packet);

    public void registerPacketHandler(ISimplePacketHandler packetHandler);

    public void registerPacket(Class<ISimplePacket> packetType, ISimplePacketHandler packetHandler);

    public void registerPacket(ISimplePacket packet, ISimplePacketHandler packetHandler);

    public IPacketDispatcher getPacketDispatcher();

}
