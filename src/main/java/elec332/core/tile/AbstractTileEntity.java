package elec332.core.tile;

import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.handler.annotations.TileEntityAnnotationProcessor;
import elec332.core.inventory.window.IWindowFactory;
import elec332.core.inventory.window.WindowManager;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.network.packets.PacketTileDataToServer;
import elec332.core.util.BlockProperties;
import elec332.core.util.NBTTypes;
import elec332.core.util.ServerHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Created by Elec332 on 3-8-2016.
 */
public class AbstractTileEntity extends TileEntity implements IElecCoreNetworkTile, RegisteredTileEntity.TypeSetter {

    public AbstractTileEntity() {
        this(null);
        this.setTileEntityType(TileEntityAnnotationProcessor.getTileType(this.getClass()));
    }

    public AbstractTileEntity(TileEntityType<?> type) {
        super(type);
        this.setTileEntityType(type);
    }

    private TileEntityType<?> type;
    private boolean isGatheringPackets;
    private Map<Integer, CompoundNBT> gatherData;

    public Direction getTileFacing() {
        return getTileFacing(BlockProperties.FACING_HORIZONTAL);
    }

    public Direction getTileFacing(Property<Direction> prop) {
        return getBlockState().get(prop);
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        readLegacy(nbt);
    }

    public void readLegacy(CompoundNBT tag) {
    }

    /*
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull BlockState oldState, @Nonnull BlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }*/

    public boolean openTileGui(PlayerEntity player) {
        if (WorldHelper.isClient(getWorld())) {
            return true;
        }
        if (!(this instanceof IWindowFactory)) {
            throw new RuntimeException();
        }
        WindowManager.openTileWindow(player, getPos());
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
    public void sendPacketToServer(int ID, CompoundNBT data) {
        ElecCore.networkHandler.sendToServer(new PacketTileDataToServer(this, ID, data));
    }

    public void onPacketReceivedFromClient(ServerPlayerEntity sender, int ID, CompoundNBT data) {
    }

    public void sendPacket(int ID, CompoundNBT data) {
        if (isGatheringPackets) {
            gatherData.put(ID, data);
            return;
        }
        for (ServerPlayerEntity player : ServerHelper.getAllPlayersWatchingBlock(getWorld(), getPos())) {
            sendPacketTo(player, ID, data);
        }
    }

    public void sendPacketTo(ServerPlayerEntity player, int ID, CompoundNBT data) {
        player.connection.sendPacket(new SUpdateTileEntityPacket(getPos(), ID, data));
    }

    public void syncTile() {
        sendPacket(0, getUpdateTag());
    }

    public void sendInitialLoadPackets() {
    }

    @Override
    @Nonnull
    @Deprecated
    public CompoundNBT getUpdateTag() {
        isGatheringPackets = true;
        gatherData = Maps.newHashMap();
        sendInitialLoadPackets();
        isGatheringPackets = false;
        CompoundNBT tag = getInitialData();
        if (!gatherData.isEmpty()) {
            ListNBT list = new ListNBT();
            gatherData.forEach((key, value) -> {
                CompoundNBT tag1 = new CompoundNBT();
                tag1.putInt("pid", key);
                tag1.put("pda", value);
                list.add(tag1);
            });
            tag.put("morePackets_eD", list);
        }
        return tag;
    }

    public CompoundNBT getInitialData() {
        return write(new CompoundNBT());
    }

    public CompoundNBT getDefaultUpdateTag() {
        return super.getUpdateTag();
    }

    @Override
    @Nonnull
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 0, getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, @Nonnull CompoundNBT tag) {
        ListNBT p = tag.getList("morePackets_eD", NBTTypes.COMPOUND.getID());
        tag.remove("morePackets_eD");
        read(state, tag);
        onDataPacket(0, tag);
        for (int i = 0; i < p.size(); i++) {
            CompoundNBT tag_ = p.getCompound(i);
            onDataPacket(tag_.getInt("pid"), tag_.getCompound("pda"));
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        if (packet.getTileEntityType() == 0) {
            handleUpdateTag(WorldHelper.getBlockState(getWorld(), getPos()), packet.getNbtCompound());
        } else {
            onDataPacket(packet.getTileEntityType(), packet.getNbtCompound());
        }
    }

    public void onDataPacket(int id, CompoundNBT tag) {
    }

    @Override
    public void setTileEntityType(TileEntityType<?> type) {
        this.type = type;
    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return type;
    }

}