package elec332.core.network;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 4-9-2015.
 */
public class PacketReRenderBlock extends AbstractPacketTileAction {

    public PacketReRenderBlock(){
    }

    public PacketReRenderBlock(TileEntity tile){
        super(tile, writeTileToNBT(tile));
    }

    private static NBTTagCompound writeTileToNBT(TileEntity tile){
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        return tag;
    }

    @Override
    public void processPacket(TileEntity tile, int id, NBTTagCompound message, MessageContext ctx) {
        if (tile != null) {
            tile.readFromNBT(message);
            tile.getWorldObj().markBlockRangeForRenderUpdate(tile.xCoord, tile.yCoord, tile.zCoord, tile.xCoord, tile.yCoord, tile.zCoord);
        }
    }

}
