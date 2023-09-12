package elec332.core.api.network.object;

import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.IPacketDispatcher;
import elec332.core.api.util.IEntityFilter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface INetworkObjectHandler<N extends INetworkObjectSender> extends ElecByteBuf.Factory {

    public void sendToAll(int id);

    public void sendTo(int id, IEntityFilter<ServerPlayerEntity> playerFilter, MinecraftServer server);

    public void sendTo(int id, List<ServerPlayerEntity> players);

    public void sendTo(int id, ServerPlayerEntity player);

    public void sendToAllAround(int id, IPacketDispatcher.TargetPoint point);

    public void sendToDimension(int id, ResourceLocation dimensionName);

    public void sendToDimension(int id, DimensionType dimensionId);

    public void sendToServer(int id);

    public void sendToAll(int id, CompoundTag data);

    public void sendTo(int id, CompoundTag data, IEntityFilter<ServerPlayerEntity> playerFilter, MinecraftServer server);

    public void sendTo(int id, CompoundTag data, List<ServerPlayerEntity> players);

    public void sendTo(int id, CompoundTag data, ServerPlayerEntity player);

    public void sendToAllAround(int id, CompoundTag data, IPacketDispatcher.TargetPoint point);

    public void sendToDimension(int id, CompoundTag data, DimensionType dimensionId);

    public void sendToDimension(int id, CompoundTag data, ResourceLocation dimensionName);

    public void sendToServer(int id, CompoundTag data);

    public void sendToAll(int id, ByteBuf data);

    default public void sendTo(int id, ByteBuf data, IEntityFilter<ServerPlayerEntity> playerFilter, MinecraftServer server) {
        for (ServerPlayerEntity player : playerFilter.filterEntities(server.getPlayerList().getPlayers())) {
            sendTo(id, data, player);
        }
    }

    default public void sendTo(int id, ByteBuf data, List<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            sendTo(id, data, player);
        }
    }

    public void sendTo(int id, ByteBuf data, ServerPlayerEntity player);

    public void sendToAllAround(int id, ByteBuf data, IPacketDispatcher.TargetPoint point);

    public void sendToDimension(int id, ByteBuf data, DimensionType dimensionId);

    public void sendToDimension(int id, ByteBuf data, ResourceLocation dimensionName);

    public void sendToServer(int id, ByteBuf data);

    @Nullable
    public N getNetworkObjectSender();

}
