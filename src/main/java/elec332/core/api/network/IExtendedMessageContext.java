package elec332.core.api.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 3-12-2016.
 */
public interface IExtendedMessageContext {

    public Side getSide();

    public EntityPlayer getSender();

    public World getWorld();

    public INetHandler getNetHandler();

    default public NetHandlerPlayServer getServerHandler() {
        return (NetHandlerPlayServer) getNetHandler();
    }

    @SideOnly(Side.CLIENT)
    default public NetHandlerPlayClient getClientHandler() {
        return (NetHandlerPlayClient) getNetHandler();
    }

    public void sendPacket(final Packet<?> packetIn);

    public NetworkManager getNetworkManager();

}
