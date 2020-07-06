package elec332.core.api.network;

import elec332.core.api.util.IEntityFilter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Elec332 on 15-10-2016.
 */
public interface IPacketDispatcher extends ElecByteBuf.Factory {

    /**
     * Returns the name of this channel.
     *
     * @return The name of this channel
     */
    public ResourceLocation getChannelName();

    /**
     * Send this message to everyone.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     */
    public void sendToAll(IMessage message);

    /**
     * Send this message to the specified players.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message      The message to send
     * @param playerFilter The selector that determines what players to send the message to.
     */
    default public void sendTo(IMessage message, IEntityFilter<ServerPlayerEntity> playerFilter, MinecraftServer server) {
        for (ServerPlayerEntity player : playerFilter.filterEntities(server.getPlayerList().getPlayers())) {
            sendTo(message, player);
        }
    }

    /**
     * Send this message to the specified players.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param players The players to send it to
     */
    default public void sendTo(IMessage message, List<ServerPlayerEntity> players) {
        players.forEach(p -> sendTo(message, p));
    }

    /**
     * Send this message to the specified players.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param players The players to send it to
     */
    default public void sendTo(IMessage message, Stream<ServerPlayerEntity> players) {
        players.forEach(p -> sendTo(message, p));
    }

    /**
     * Send this message to the specified player.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param player  The player to send it to
     */
    public void sendTo(IMessage message, ServerPlayerEntity player);

    /**
     * Send this message to everyone within a certain range of a point defined in the packet.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param world   The world in which the point is located
     * @param range   The range
     */
    default public <M extends IMessage & ILocatedPacket> void sendToAllAround(M message, World world, double range) {
        sendToAllAround(message, message.getTargetPoint(world, range));
    }

    /**
     * Send this message to everyone within a certain range of a point.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param world   The world to which to send
     * @param pos     The position around which to send
     * @param range   The range around the position
     */
    public void sendToAllAround(IMessage message, IWorld world, BlockPos pos, double range);

    /**
     * Send this message to everyone within a certain range of a point.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message The message to send
     * @param point   The {@link TargetPoint} around which to send
     */
    public void sendToAllAround(IMessage message, TargetPoint point);

    /**
     * Send this message to everyone within the supplied dimension.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message       The message to send
     * @param dimensionName The dimension to target
     */
    public void sendToDimension(IMessage message, ResourceLocation dimensionName);

    /**
     * Send this message to everyone within the supplied dimension.
     * The message handler for this message type should be on the CLIENT side.
     *
     * @param message     The message to send
     * @param dimensionId The dimension id to target
     */
    public void sendToDimension(IMessage message, DimensionType dimensionId);

    /**
     * Send this message to the server.
     * The message handler for this message type should be on the SERVER side.
     *
     * @param message The message to send
     */
    public void sendToServer(IMessage message);

    public static class TargetPoint {

        /**
         * A target point
         *
         * @param dimension The dimension to target
         * @param x         The X coordinate
         * @param y         The Y coordinate
         * @param z         The Z coordinate
         * @param range     The range
         */
        public TargetPoint(DimensionType dimension, double x, double y, double z, double range) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.range = range;
            this.dimension = dimension;
        }

        public final double x;
        public final double y;
        public final double z;
        public final double range;
        public final DimensionType dimension;

    }

}
