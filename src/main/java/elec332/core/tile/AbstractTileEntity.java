package elec332.core.tile;

import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.inventory.window.IWindowHandler;
import elec332.core.inventory.window.WindowManager;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.network.packets.PacketTileDataToServer;
import elec332.core.util.NBTTypes;
import elec332.core.util.ServerHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Created by Elec332 on 3-8-2016.
 */
public class AbstractTileEntity extends TileEntity implements IElecCoreNetworkTile {

    private boolean isGatheringPackets;
    private Map<Integer, NBTTagCompound> gatherData;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Deprecated
    public boolean openGui(EntityPlayer player, Object mod, int guiID) {
        player.openGui(mod, guiID, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
        return true;
    }

    public boolean openWindow(EntityPlayer player, IWindowHandler windowHandler, int id) {
        WindowManager.openWindow(player, windowHandler, getWorld(), pos, (byte) id);
        return true;
    }

    public void reRenderBlock() {
        WorldHelper.markBlockForRenderUpdate(world, pos);
    }

    public void notifyNeighborsOfChange() {
        WorldHelper.notifyNeighborsOfStateChange(getWorld(), pos, blockType);
    }

    //NETWORK///////////////////////

    public void syncData() {
        WorldHelper.markBlockForUpdate(getWorld(), pos);
    }

    @SideOnly(Side.CLIENT)
    public void sendPacketToServer(int ID, NBTTagCompound data) {
        ElecCore.networkHandler.sendToServer(new PacketTileDataToServer(this, ID, data));
    }

    public void onPacketReceivedFromClient(EntityPlayerMP sender, int ID, NBTTagCompound data) {
    }

    public void sendPacket(int ID, NBTTagCompound data) {
        if (isGatheringPackets) {
            gatherData.put(ID, data);
            return;
        }
        for (EntityPlayerMP player : ServerHelper.getAllPlayersWatchingBlock(getWorld(), getPos())) {
            sendPacketTo(player, ID, data);
        }
    }

    public void sendPacketTo(EntityPlayerMP player, int ID, NBTTagCompound data) {
        player.connection.sendPacket(new SPacketUpdateTileEntity(getPos(), ID, data));
    }

    public void syncTile() {
        sendPacket(0, getUpdateTag());
    }

    public void sendInitialLoadPackets() {
    }

    @Override
    @Nonnull
    @Deprecated
    @SuppressWarnings("all")
    public NBTTagCompound getUpdateTag() {
        isGatheringPackets = true;
        gatherData = Maps.newHashMap();
        sendInitialLoadPackets();
        isGatheringPackets = false;
        NBTTagCompound tag = getInitialData();
        if (!gatherData.isEmpty()) {
            NBTTagList list = new NBTTagList();
            gatherData.forEach((key, value) -> {
                NBTTagCompound tag1 = new NBTTagCompound();
                tag1.setInteger("pid", key);
                tag1.setTag("pda", value);
                list.appendTag(tag1);
            });
            tag.setTag("morePackets_eD", list);
        }
        return tag;
    }

    public NBTTagCompound getInitialData() {
        return writeToNBT(new NBTTagCompound());
    }

    public NBTTagCompound getDefaultUpdateTag() {
        return super.getUpdateTag();
    }

    @Override
    @Nonnull
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        NBTTagList p = tag.getTagList("morePackets_eD", NBTTypes.COMPOUND.getID());
        tag.removeTag("morePackets_eD");
        readFromNBT(tag);
        onDataPacket(0, tag);
        for (int i = 0; i < p.tagCount(); i++) {
            NBTTagCompound tag_ = p.getCompoundTagAt(i);
            onDataPacket(tag_.getInteger("pid"), tag_.getCompoundTag("pda"));
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        if (packet.getTileEntityType() == 0) {
            readFromNBT(packet.getNbtCompound());
            onDataPacket(0, packet.getNbtCompound());
        } else {
            onDataPacket(packet.getTileEntityType(), packet.getNbtCompound());
        }
    }

    public void onDataPacket(int id, NBTTagCompound tag) {
    }

}