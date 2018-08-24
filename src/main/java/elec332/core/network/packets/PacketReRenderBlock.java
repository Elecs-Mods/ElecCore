package elec332.core.network.packets;

import elec332.core.world.WorldHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by Elec332 on 4-9-2015.
 */
public class PacketReRenderBlock extends AbstractPacketTileAction {

    public PacketReRenderBlock() {
    }

    public PacketReRenderBlock(TileEntity tile) {
        super(tile, writeTileToNBT(tile));
    }

    private static NBTTagCompound writeTileToNBT(TileEntity tile) {
        NBTTagCompound tag = new NBTTagCompound();
        tile.writeToNBT(tag);
        return tag;
    }

    @Override
    public void processPacket(TileEntity tile, int id, NBTTagCompound message, MessageContext ctx) {
        if (tile != null) {
            tile.readFromNBT(message);
            WorldHelper.markBlockForRenderUpdate(tile.getWorld(), tile.getPos());
        }
    }

}
