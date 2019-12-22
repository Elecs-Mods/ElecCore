package elec332.core.util;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import elec332.core.api.network.IMessage;
import elec332.core.api.network.INetworkHandler;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ServerHelper {

    /**
     * @return All online players
     */
    public static List<ServerPlayerEntity> getOnlinePlayers() {
        return getMinecraftServer().getPlayerList().getPlayers();
    }

    /**
     * Checks if the specified player is online
     *
     * @param uuid The player UUID to be checked
     * @return Whether the specified player is online
     */
    public static boolean isPlayerOnline(UUID uuid) {
        return getOnlinePlayers().stream().anyMatch((Predicate<ServerPlayerEntity>) player -> PlayerHelper.getPlayerUUID(player).equals(uuid));
    }

    /**
     * Gets a {@link ServerPlayerEntity} by its UUID, returns null if the player is offline/not found
     *
     * @param uuid The player UUID
     * @return The player whose UUID matches the one provided, can be null
     */
    @Nullable
    public static ServerPlayerEntity getPlayer(UUID uuid) {
        return getOnlinePlayers().stream()
                .filter((Predicate<ServerPlayerEntity>) player -> PlayerHelper.getPlayerUUID(player).equals(uuid))
                .findFirst() //The returned stream is lazy, so this is perfectly fine
                .orElse(null);
    }

    /**
     * Gets all players who have the provided position loaded,
     * and need to be notified of changes to it
     *
     * @param world The world in which the position is located
     * @param pos   The position
     * @return All players who need to be notified of chenges to the specified location
     */
    public static List<ServerPlayerEntity> getAllPlayersWatchingBlock(World world, BlockPos pos) {
        return getAllPlayersWatchingBlock(world, pos.getX(), pos.getZ());
    }

    /**
     * Gets all players who have the chunk in which the provided (x,z) coordinates are located loaded,
     * and need to be notified of changes to it
     *
     * @param world The world in which the position is located
     * @param x     The x coordinate
     * @param z     The z coordinate
     * @return All players who need to be notified of chenges to the specified location
     */
    public static List<ServerPlayerEntity> getAllPlayersWatchingBlock(World world, int x, int z) {
        if (world instanceof ServerWorld) {
            ChunkHolder.IPlayerProvider playerManager = WorldHelper.getPlayerManager((ServerWorld) world);
            return playerManager.getTrackingPlayers(WorldHelper.chunkPosFromBlockPos(new BlockPos(x, 0, z)), false)
                    .collect(Collectors.toList());
        }
        return ImmutableList.of();
    }

    /**
     * Sends a message to all players who have the provided position loaded,
     * and need to be notified of changes to it
     *
     * @param world          The world in which the position is located
     * @param pos            The position
     * @param message        The message to be sent
     * @param networkHandler The network-handler who has to send the messages
     */
    public static void sendMessageToAllPlayersWatchingBlock(World world, BlockPos pos, IMessage message, INetworkHandler networkHandler) {
        getAllPlayersWatchingBlock(world, pos).forEach(player -> networkHandler.sendTo(message, player));
    }

    /**
     * Fetches all players in the world, by dimension ID
     *
     * @param dimension The dimension ID
     * @return All players in the provided dimension
     */
    public static List<ServerPlayerEntity> getAllPlayersInDimension(final DimensionType dimension) {
        return getOnlinePlayers().stream()
                .filter((Predicate<ServerPlayerEntity>) player -> WorldHelper.getDimID(player.getEntityWorld()) == dimension)
                .collect(Collectors.toList());
    }

    /**
     * FSends a message to all players in the world, by dimension ID
     *
     * @param dimension      The dimension ID
     * @param message        The message to be sent
     * @param networkHandler The network-handler who has to send the messages
     */
    public static void sendMessageToAllPlayersInDimension(DimensionType dimension, IMessage message, INetworkHandler networkHandler) {
        getAllPlayersInDimension(dimension).forEach(playerMP -> networkHandler.sendTo(message, playerMP));
    }

    public static MinecraftServer getMinecraftServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

}
