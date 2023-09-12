package elec332.core.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundTag;

/**
 * Created by Elec332 on 25-8-2015.
 * <p>
 * Used for sending packets between/to {@link net.minecraft.tileentity.TileEntity}'s
 */
public interface IElecCoreNetworkTile {

    /**
     * Called on the server side when a packet was received from the client
     *
     * @param sender The player that sent the message
     * @param ID     The message ID
     * @param data   The message data
     */
    public void onPacketReceivedFromClient(ServerPlayerEntity sender, int ID, CompoundTag data);

    /**
     * Gets called when a message has been received from the server
     *
     * @param id  The message ID
     * @param tag The message data
     */
    public void onDataPacket(int id, CompoundTag tag);

}
