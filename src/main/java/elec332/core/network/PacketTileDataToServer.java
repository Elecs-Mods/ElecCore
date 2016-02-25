package elec332.core.network;

import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Elec332 on 15-8-2015.
 */
@SuppressWarnings("unused")
public class PacketTileDataToServer extends AbstractPacket {

    public PacketTileDataToServer(){
    }

    public PacketTileDataToServer(IElecCoreNetworkTile tile, int ID, NBTTagCompound data){
        super(new NBTHelper().addToTag(ID, "PacketId").addToTag(data, "data").addToTag(((TileEntity)tile).getPos()).serializeNBT());
    }

    @Override
    public IMessage onMessageThreadSafe(AbstractPacket message, MessageContext ctx) {
        NBTHelper data = new NBTHelper(message.networkPackageObject);
        int ID = data.getInteger("PacketId");
        EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        IElecCoreNetworkTile tile = (IElecCoreNetworkTile) WorldHelper.getTileAt(sender.worldObj, data.getPos());
        if (tile != null) {
            tile.onPacketReceivedFromClient(sender, ID, message.networkPackageObject.getCompoundTag("data"));
        }
        return null;
    }
}
