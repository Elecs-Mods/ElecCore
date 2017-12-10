package elec332.core.api.network;

import com.google.common.base.Preconditions;
import elec332.core.api.util.IEntityFilter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

import java.util.List;

/**
 * Created by Elec332 on 15-10-2016.
 */
public interface IPacketDispatcher extends ElecByteBuf.Factory {

    /**
     * Returns the name of this channel.
     *
     * @return The name of this channel
     */
    public String getChannelName();

    /**
     * Send this message to everyone.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     */
    public void sendToAll(IMessage message);

    /**
     * Send this message to the specified players.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param playerFilter The selector that determines what players to send the message to.
     */
    default public void sendTo(IMessage message, IEntityFilter<EntityPlayerMP> playerFilter, MinecraftServer server){
        for (EntityPlayerMP player : playerFilter.filterEntities(server.getPlayerList().getPlayers())){
            sendTo(message, player);
        }
    }

    /**
     * Send this message to the specified players.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param players The players to send it to
     */
    default public void sendTo(IMessage message, List<EntityPlayerMP> players){
        for (EntityPlayerMP player : players){
            sendTo(message, player);
        }
    }

    /**
     * Send this message to the specified player.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param player The player to send it to
     */
    public void sendTo(IMessage message, EntityPlayerMP player);

    /**
     * Send this message to everyone within a certain range of a point defined in the packet.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param world The world in which the point is located
     * @param range The range
     */
    default public <M extends IMessage & ILocatedPacket> void sendToAllAround(M message, World world, double range){
        sendToAllAround(message, message.getTargetPoint(world, range));
    }

    /**
     * Send this message to everyone within a certain range of a point.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param world The world to which to send
     * @param pos The position around which to send
     * @param range The range around the position
     */
    default public void sendToAllAround(IMessage message, World world, BlockPos pos, double range){
        sendToAllAround(message, new NetworkRegistry.TargetPoint(Preconditions.checkNotNull(world.provider).getDimension(), pos.getX(), pos.getY(), pos.getZ(), range));
    }

    /**
     * Send this message to everyone within a certain range of a point.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param point The {@link NetworkRegistry.TargetPoint} around which to send
     */
    public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point);

    /**
     * Send this message to everyone within the supplied dimension.
     * The {@link IMessageHandler} for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param dimensionId The dimension id to target
     */
    public void sendToDimension(IMessage message, int dimensionId);

    /**
     * Send this message to the server.
     * The {@link IMessageHandler} for this message type should be on the SERVER side.
     *
     * @param message The message to send
     */
    public void sendToServer(IMessage message);

}
