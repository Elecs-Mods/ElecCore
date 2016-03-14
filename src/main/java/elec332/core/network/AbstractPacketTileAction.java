package elec332.core.network;

import elec332.core.util.NBTHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 4-9-2015.
 */
public abstract class AbstractPacketTileAction extends AbstractPacket {

    public AbstractPacketTileAction(){
    }

    public AbstractPacketTileAction(TileEntity tile, NBTTagCompound message){
        this(tile, message, 0);
    }

    public AbstractPacketTileAction(TileEntity tile, NBTTagCompound message, int id){
        super(new NBTHelper().addToTag(message, "data").addToTag(id, "id").addToTag(tile.getPos()).serializeNBT());
    }

    @Override
    public IMessage onMessageThreadSafe(AbstractPacket message, MessageContext ctx) {
        NBTHelper tag = new NBTHelper(message.networkPackageObject);
        BlockPos loc = tag.getPos();
        int i = tag.getInteger("id");
        NBTTagCompound data = tag.getCompoundTag("data");
        if (ctx.side == Side.CLIENT)
            return processClient(loc, i, data, ctx);
        processPacket(WorldHelper.getTileAt(ctx.getServerHandler().playerEntity.getServerForPlayer(), loc), i, data, ctx);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private IMessage processClient(BlockPos loc, int id, NBTTagCompound data, MessageContext ctx){
        processPacket(WorldHelper.getTileAt(net.minecraft.client.Minecraft.getMinecraft().theWorld, loc), id, data, ctx);
        return null;
    }

    public abstract void processPacket(TileEntity tile, int id, NBTTagCompound message, MessageContext ctx);

}
