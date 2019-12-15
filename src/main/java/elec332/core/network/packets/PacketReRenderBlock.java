package elec332.core.network.packets;

import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.world.WorldHelper;
import net.minecraft.nbt.CompoundNBT;
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

    private static CompoundNBT writeTileToNBT(TileEntity tile) {
        CompoundNBT tag = new CompoundNBT();
        tile.write(tag);
        return tag;
    }

    @Override
    public void processPacket(World world, TileEntity tile, int id, CompoundNBT message, IExtendedMessageContext ctx) {
        if (tile != null) {
            tile.read(message);
            WorldHelper.markBlockForRenderUpdate(world, tile.getPos());
        }
    }

}
