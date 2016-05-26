package elec332.core.tile;

import com.google.common.collect.Lists;
import elec332.core.main.ElecCore;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.network.PacketTileDataToServer;
import elec332.core.server.ServerHelper;
import elec332.core.util.BlockStateHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 8-4-2015.
 */
@SuppressWarnings("unused")
public class TileBase extends TileEntity implements IElecCoreNetworkTile {

    @Override
    public void validate() {
        super.validate();
        ElecCore.tickHandler.registerCall(new Runnable() {
            @Override
            public void run() {
                if (WorldHelper.chunkLoaded(worldObj, getPos())) {
                    onTileLoaded();
                }
            }
        }, getWorld());
    }


    public void invalidate() {
        if (!isInvalid()){
            super.invalidate();
            onTileUnloaded();
        }
    }

    public void onChunkUnload() {
        //if (!isInvalid()) {
            super.onChunkUnload();
            //super.invalidate();
            onTileUnloaded();
        //}
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        readItemStackNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        writeToItemStack(tagCompound);
        return tagCompound;
    }

    /**
     * Reads NBT data from either the NBTTagCompound from readFromNBT when a tile loads,
     * or the ItemStack, if the block is placed.
     *
     * @param tagCompound The NBT Tag compound
     */
    public void readItemStackNBT(NBTTagCompound tagCompound){
    }


    /**
     * Writes the NBT data, gets run when the tile unloads and when the block is broken,
     * when the block is broken, this data will be stored on the ItemStack, if the tile unloads,
     * this gets treated as the writeToNBT method.
     *
     * @param tagCompound The NBT Tag compound
     */
    public void writeToItemStack(NBTTagCompound tagCompound){
    }

    public void onTileLoaded(){
    }

    public void onTileUnloaded(){
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Deprecated
    public void notifyNeighboursOfDataChange(){
        this.markDirty();
        this.worldObj.notifyNeighborsOfStateChange(getPos(), blockType);
    }

    public EnumFacing getTileFacing(){
        return getBlockType().getStateFromMeta(getBlockMetadata()).getValue(BlockStateHelper.FACING_NORMAL.getProperty());
    }

    public boolean timeCheck() {
        return this.worldObj.getTotalWorldTime() % 32L == 0L;
    }

    public int getLightOpacity() {
        return 0;
    }

    public int getLightValue() {
        return 0;
    }

    public void onBlockRemoved() {

    }

    public void onBlockAdded() {

    }

    public void onBlockPlacedBy(EntityLivingBase entityLiving, ItemStack stack) {
        if (stack.hasTagCompound())
            readItemStackNBT(stack.getTagCompound());
    }

    public void onNeighborBlockChange(Block block) {

    }

    public void onNeighborTileChange(BlockPos neighbor){

    }

    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    public void onBlockClicked(EntityPlayer player){
    }

    public List<ItemStack> getDrops(int fortune){
        return Lists.newArrayList(itemStackFromNBTTile());
    }

    public void onWrenched(EnumFacing forgeDirection) {
        if ((forgeDirection != EnumFacing.UP && forgeDirection != EnumFacing.DOWN) || canFaceUpOrDown()) {
            setFacing(forgeDirection);
            markDirty();
            syncData();
            reRenderBlock();
        }
    }

    public boolean canFaceUpOrDown(){
        return false;
    }

    public boolean openGui(EntityPlayer player, Object mod, int guiID){
        player.openGui(mod, guiID, worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
        return true;
    }

    protected ItemStack itemStackFromNBTTile(){
        NBTTagCompound compound = new NBTTagCompound();
        ItemStack ret = new ItemStack(getBlockType(), 1, getBlockMetadata());
        writeToItemStack(compound);
        ret.setTagCompound(compound);
        return ret;
    }

    public void setFacing(EnumFacing facing){
        WorldHelper.setBlockState(worldObj, pos, getBlockType().getBlockState().getBaseState().withProperty(BlockStateHelper.FACING_NORMAL.getProperty(), facing), 2);
        notifyNeighborsOfChange();
    }

    public void reRenderBlock(){
        WorldHelper.reRenderBlock(this);
    }

    public void notifyNeighborsOfChange(){
        worldObj.notifyNeighborsOfStateChange(pos, blockType);
    }

    //NETWORK///////////////////////

    public void syncData(){
        WorldHelper.markBlockForUpdate(worldObj, pos);
    }

    @SideOnly(Side.CLIENT)
    public void sendPacketToServer(int ID, NBTTagCompound data){
        ElecCore.networkHandler.getNetworkWrapper().sendToServer(new PacketTileDataToServer(this, ID, data));
    }

    public void onPacketReceivedFromClient(EntityPlayerMP sender, int ID, NBTTagCompound data){
    }

    public void sendPacket(int ID, NBTTagCompound data){
        for (EntityPlayerMP player : ServerHelper.instance.getAllPlayersWatchingBlock(worldObj, getPos()))
            sendPacketTo(player, ID, data);
    }

    public void sendPacketTo(EntityPlayerMP player, int ID, NBTTagCompound data){
        player.connection.sendPacket(new SPacketUpdateTileEntity(getPos(), ID, data));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        if (packet.getTileEntityType() == 0)
            readFromNBT(packet.getNbtCompound());
        else onDataPacket(packet.getTileEntityType(), packet.getNbtCompound());
    }

    public void onDataPacket(int id, NBTTagCompound tag){
    }

}
