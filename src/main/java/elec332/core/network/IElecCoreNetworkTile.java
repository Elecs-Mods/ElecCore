package elec332.core.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 25-8-2015.
 *
 * Used for sending packets between/to {@link net.minecraft.tileentity.TileEntity}'s
 */
public interface IElecCoreNetworkTile {

    /**
     * Called on the server side when a packet was received from the client
     *
     * @param sender The player that sent the message
     * @param ID The message ID
     * @param data The message data
     */
    public void onPacketReceivedFromClient(EntityPlayerMP sender, int ID, NBTTagCompound data);

    /**
     * Gets called when a message has been received from the server
     *
     * @param id The message ID
     * @param tag The message data
     */
    public void onDataPacket(int id, NBTTagCompound tag);

}
