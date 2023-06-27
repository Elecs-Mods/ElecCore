package elec332.core.api.network.object;

import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.IPacketDispatcher;
import elec332.core.api.util.IEntityFilter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface INetworkObjectHandler<N extends INetworkObjectSender<N>> extends ElecByteBuf.Factory {

    void sendToAll(int id);

    void sendTo(int id, IEntityFilter<ServerPlayerEntity> playerFilter, MinecraftServer server);

    void sendTo(int id, List<ServerPlayerEntity> players);

    void sendTo(int id, ServerPlayerEntity player);

    void sendToAllAround(int id, IPacketDispatcher.TargetPoint point);

    void sendToDimension(int id, ResourceLocation dimensionName);

    void sendToDimension(int id, RegistryKey<World> dimensionId);

    void sendToServer(int id);

    void sendToAll(int id, CompoundNBT data);

    void sendTo(int id, CompoundNBT data, IEntityFilter<ServerPlayerEntity> playerFilter, MinecraftServer server);

    void sendTo(int id, CompoundNBT data, List<ServerPlayerEntity> players);

    void sendTo(int id, CompoundNBT data, ServerPlayerEntity player);

    void sendToAllAround(int id, CompoundNBT data, IPacketDispatcher.TargetPoint point);

    void sendToDimension(int id, CompoundNBT data, RegistryKey<World> dimensionId);

    void sendToDimension(int id, CompoundNBT data, ResourceLocation dimensionName);

    void sendToServer(int id, CompoundNBT data);

    void sendToAll(int id, ByteBuf data);

    default void sendTo(int id, ByteBuf data, IEntityFilter<ServerPlayerEntity> playerFilter, MinecraftServer server) {
        for (ServerPlayerEntity player : playerFilter.filterEntities(server.getPlayerList().getPlayers())) {
            sendTo(id, data, player);
        }
    }

    default void sendTo(int id, ByteBuf data, List<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            sendTo(id, data, player);
        }
    }

    void sendTo(int id, ByteBuf data, ServerPlayerEntity player);

    void sendToAllAround(int id, ByteBuf data, IPacketDispatcher.TargetPoint point);

    void sendToDimension(int id, ByteBuf data, RegistryKey<World> dimensionId);

    void sendToDimension(int id, ByteBuf data, ResourceLocation dimensionName);

    void sendToServer(int id, ByteBuf data);

    @Nullable
    N getNetworkObjectSender();

}
