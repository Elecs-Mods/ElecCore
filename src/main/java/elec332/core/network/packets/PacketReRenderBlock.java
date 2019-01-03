package elec332.core.network.packets;

import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.world.WorldHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
        tile.write(tag);
        return tag;
    }

    @Override
    public void processPacket(World world, TileEntity tile, int id, NBTTagCompound message, IExtendedMessageContext ctx) {
        if (tile != null) {
            tile.read(message);
            WorldHelper.markBlockForRenderUpdate(world, tile.getPos());
        }
    }

}
