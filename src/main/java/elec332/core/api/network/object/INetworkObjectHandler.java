package elec332.core.api.network.object;

import elec332.core.api.util.IEntityFilter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface INetworkObjectHandler<N extends INetworkObjectSender> {

    public void sendToAll(int id);

    public void sendTo(int id, IEntityFilter<EntityPlayerMP> playerFilter, MinecraftServer server);

    public void sendTo(int id, List<EntityPlayerMP> players);

    public void sendTo(int id, EntityPlayerMP player);

    public void sendToAllAround(int id, NetworkRegistry.TargetPoint point);

    public void sendToDimension(int id, int dimensionId);

    public void sendToServer(int id);

    public void sendToAll(int id, NBTTagCompound data);

    public void sendTo(int id, NBTTagCompound data, IEntityFilter<EntityPlayerMP> playerFilter, MinecraftServer server);

    public void sendTo(int id, NBTTagCompound data, List<EntityPlayerMP> players);

    public void sendTo(int id, NBTTagCompound data, EntityPlayerMP player);

    public void sendToAllAround(int id, NBTTagCompound data, NetworkRegistry.TargetPoint point);

    public void sendToDimension(int id, NBTTagCompound data, int dimensionId);

    public void sendToServer(int id, NBTTagCompound data);

    public void sendToAll(int id, ByteBuf data);

    default public void sendTo(int id, ByteBuf data, IEntityFilter<EntityPlayerMP> playerFilter, MinecraftServer server){
        for (EntityPlayerMP player : playerFilter.filterEntities(server.getPlayerList().getPlayerList())){
            sendTo(id, data, player);
        }
    }

    default public void sendTo(int id, ByteBuf data, List<EntityPlayerMP> players){
        for (EntityPlayerMP player : players){
            sendTo(id, data, player);
        }
    }

    public void sendTo(int id, ByteBuf data, EntityPlayerMP player);

    public void sendToAllAround(int id, ByteBuf data, NetworkRegistry.TargetPoint point);

    public void sendToDimension(int id, ByteBuf data, int dimensionId);

    public void sendToServer(int id, ByteBuf data);

    @Nullable
    public N getNetworkObjectSender();

}
