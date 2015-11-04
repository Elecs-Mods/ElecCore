package elec332.core.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.util.BlockLoc;
import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 15-8-2015.
 */
public class PacketTileDataToServer extends AbstractPacket {

    public PacketTileDataToServer(){
    }

    public PacketTileDataToServer(IElecCoreNetworkTile tile, int ID, NBTTagCompound data){
        super(new BlockLoc((TileEntity)tile).toNBT(new NBTHelper().addToTag(ID, "PacketId").addToTag(data, "data").toNBT()));
    }

    @Override
    public IMessage onMessage(AbstractPacket message, MessageContext ctx) {
        int ID = message.networkPackageObject.getInteger("PacketId");
        EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        IElecCoreNetworkTile tile = (IElecCoreNetworkTile) WorldHelper.getTileAt(sender.worldObj, new BlockLoc(message.networkPackageObject));
        if (tile != null) {
            tile.onPacketReceivedFromClient(sender, ID, message.networkPackageObject.getCompoundTag("data"));
        }
        return null;
    }
}
