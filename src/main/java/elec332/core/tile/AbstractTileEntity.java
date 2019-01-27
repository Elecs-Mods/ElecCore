package elec332.core.tile;

import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.MC113ToDoReference;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.handler.annotations.TileEntityAnnotationProcessor;
import elec332.core.inventory.window.IWindowHandler;
import elec332.core.inventory.window.WindowManager;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.network.packets.PacketTileDataToServer;
import elec332.core.util.NBTTypes;
import elec332.core.util.ServerHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Created by Elec332 on 3-8-2016.
 */
public class AbstractTileEntity extends TileEntity implements IElecCoreNetworkTile, RegisteredTileEntity.TypeSetter {

    @SuppressWarnings("all")
    public AbstractTileEntity(){
        super(null);
        setTileEntityType(TileEntityAnnotationProcessor.getTileType(getClass()));
    }

    public AbstractTileEntity(TileEntityType<?> type) {
        super(type);
        this.type = type;
    }

    private TileEntityType<?> type;
    private boolean isGatheringPackets;
    private Map<Integer, NBTTagCompound> gatherData;

    /*
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }*/

    @Deprecated
    public boolean openGui(EntityPlayer player, Object mod, int guiID) {
        MC113ToDoReference.update();
        //player.openGui(mod, guiID, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
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
        WorldHelper.notifyNeighborsOfStateChange(getWorld(), pos, getBlockState().getBlock());
    }

    //NETWORK///////////////////////

    public void syncData() {
        WorldHelper.markBlockForUpdate(getWorld(), pos);
    }

    @OnlyIn(Dist.CLIENT)
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
                tag1.putInt("pid", key);
                tag1.put("pda", value);
                list.add(tag1);
            });
            tag.put("morePackets_eD", list);
        }
        return tag;
    }

    public NBTTagCompound getInitialData() {
        return write(new NBTTagCompound());
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
        NBTTagList p = tag.getList("morePackets_eD", NBTTypes.COMPOUND.getID());
        tag.remove("morePackets_eD");
        read(tag);
        onDataPacket(0, tag);
        for (int i = 0; i < p.size(); i++) {
            NBTTagCompound tag_ = p.getCompound(i);
            onDataPacket(tag_.getInt("pid"), tag_.getCompound("pda"));
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        if (packet.getTileEntityType() == 0) {
            read(packet.getNbtCompound());
            onDataPacket(0, packet.getNbtCompound());
        } else {
            onDataPacket(packet.getTileEntityType(), packet.getNbtCompound());
        }
    }

    public void onDataPacket(int id, NBTTagCompound tag) {
    }

    @Override
    public void setTileEntityType(TileEntityType<?> type) {

    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return type;
    }

}