package elec332.core.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 25-8-2015.
 */
public interface IElecCoreNetworkTile {

    public void onPacketReceivedFromClient(EntityPlayerMP sender, int ID, NBTTagCompound data);

    public void onDataPacket(int id, NBTTagCompound tag);

}
