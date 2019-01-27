package elec332.core.network.packets;

import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.util.NBTBuilder;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 15-8-2015.
 */
@SuppressWarnings("unused")
public class PacketTileDataToServer extends AbstractPacket {

    public PacketTileDataToServer() {
    }

    public PacketTileDataToServer(IElecCoreNetworkTile tile, int ID, NBTTagCompound data) {
        super(new NBTBuilder().setInteger("PacketId", ID).setTag("data", data).setBlockPos(((TileEntity) tile).getPos()).serializeNBT());
    }

    @Override
    public void onMessageThreadSafe(NBTTagCompound message, IExtendedMessageContext ctx) {
        NBTBuilder data = new NBTBuilder(message);
        int ID = data.getInteger("PacketId");
        EntityPlayerMP sender = (EntityPlayerMP) ctx.getSender();
        IElecCoreNetworkTile tile = (IElecCoreNetworkTile) WorldHelper.getTileAt(sender.getEntityWorld(), data.getBlockPos());
        if (tile != null) {
            tile.onPacketReceivedFromClient(sender, ID, message.getCompound("data"));
        }
    }
}
