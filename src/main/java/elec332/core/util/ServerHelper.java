package elec332.core.util;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import elec332.core.api.network.INetworkHandler;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ServerHelper  {

    public static List<EntityPlayerMP> getOnlinePlayers(){
        return getMinecraftServer().getPlayerList().getPlayers();
    }

    public static boolean isPlayerOnline(UUID uuid){
        return getOnlinePlayers().stream().anyMatch((Predicate<EntityPlayerMP>) player -> PlayerHelper.getPlayerUUID(player).equals(uuid));
    }

    @Nullable
    public static EntityPlayerMP getPlayer(UUID uuid){
        return getOnlinePlayers().stream()
                .filter((Predicate<EntityPlayerMP>) player -> PlayerHelper.getPlayerUUID(player).equals(uuid))
                .findFirst() //The returned stream is lazy, so this is perfectly fine
                .orElse(null);
    }

    public static List<EntityPlayerMP> getAllPlayersWatchingBlock(World world, BlockPos pos){
        return getAllPlayersWatchingBlock(world, pos.getX(), pos.getZ());
    }

    public static List<EntityPlayerMP> getAllPlayersWatchingBlock(World world, int x, int z){
        if (world instanceof WorldServer) {
            PlayerChunkMap playerManager = ((WorldServer) world).getPlayerChunkMap();
            return getOnlinePlayers().stream()
                    .filter((Predicate<EntityPlayerMP>) player -> playerManager.isPlayerWatchingChunk(player, x >> 4, z >> 4))
                    .collect(Collectors.toList());
        }
        return ImmutableList.of();
    }

    public static void sendMessageToAllPlayersWatchingBlock(World world, BlockPos pos, IMessage message, INetworkHandler networkHandler){
        getAllPlayersWatchingBlock(world, pos).forEach(player -> networkHandler.sendTo(message, player));
    }

    public static List<EntityPlayerMP> getAllPlayersInDimension(final int dimension){
        return getOnlinePlayers().stream()
                .filter((Predicate<EntityPlayerMP>) player -> WorldHelper.getDimID(player.getEntityWorld()) == dimension)
                .collect(Collectors.toList());
    }

    public static void sendMessageToAllPlayersInDimension(int dimension, IMessage message, INetworkHandler networkHandler){
        getAllPlayersInDimension(dimension).forEach(playerMP -> networkHandler.sendTo(message, playerMP));
    }

    public static MinecraftServer getMinecraftServer(){
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

}
