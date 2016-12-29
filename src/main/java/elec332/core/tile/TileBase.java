package elec332.core.tile;

import com.google.common.collect.Lists;
import elec332.core.main.ElecCore;
import elec332.core.network.IElecCoreNetworkTile;
import elec332.core.util.BlockStateHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 8-4-2015.
 */
@SuppressWarnings("unused")
public class TileBase extends TileEntityBase implements IElecCoreNetworkTile {

    public static void setWorld(TileEntity tile, World world){
        tile.setWorldObj(world);
    }

    @Override
    public void validate() {
        super.validate();
        ElecCore.tickHandler.registerCall(new Runnable() {
            @Override
            public void run() {
                if (WorldHelper.chunkLoaded(getWorld(), getPos())) {
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
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        writeToItemStack(tagCompound);
        return tagCompound;
    }

    /**
     * Reads NBTTypes data from either the NBTTagCompound from readFromNBT when a tile loads,
     * or the ItemStack, if the block is placed.
     *
     * @param tagCompound The NBTTypes Tag compound
     */
    public void readItemStackNBT(NBTTagCompound tagCompound){
    }


    /**
     * Writes the NBTTypes data, gets run when the tile unloads and when the block is broken,
     * when the block is broken, this data will be stored on the ItemStack, if the tile unloads,
     * this gets treated as the writeToNBT method.
     *
     * @param tagCompound The NBTTypes Tag compound
     */
    public void writeToItemStack(NBTTagCompound tagCompound){
    }

    public void onTileLoaded(){
    }

    public void onTileUnloaded(){
    }

    @Deprecated
    public void notifyNeighboursOfDataChange(){
        this.markDirty();
        WorldHelper.notifyNeighborsOfStateChange(getWorld(), getPos(), blockType);
    }

    @SuppressWarnings("deprecation")
    public EnumFacing getTileFacing(){
        return getBlockType().getStateFromMeta(getBlockMetadata()).getValue(BlockStateHelper.FACING_NORMAL.getProperty());
    }

    public boolean timeCheck() {
        return this.getWorld().getTotalWorldTime() % 32L == 0L;
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

    public boolean onBlockActivated(IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    public void onBlockClicked(EntityPlayer player){
    }

    public List<ItemStack> getDrops(int fortune){
        return Lists.newArrayList(itemStackFromNBTTile());
    }

    public boolean onWrenched(EnumFacing forgeDirection) {
        /*if ((forgeDirection != EnumFacing.UP && forgeDirection != EnumFacing.DOWN) || canFaceUpOrDown()) {
            setFacing(forgeDirection);
            markDirty();
            syncData();
            reRenderBlock();
        }*/
        return false;
    }

    public boolean canFaceUpOrDown(){
        return false;
    }

    protected ItemStack itemStackFromNBTTile(){
        NBTTagCompound compound = new NBTTagCompound();
        ItemStack ret = new ItemStack(getBlockType(), 1, getBlockMetadata());
        writeToItemStack(compound);
        ret.setTagCompound(compound);
        return ret;
    }

    public void setFacing(EnumFacing facing){
        WorldHelper.setBlockState(getWorld(), pos, getBlockType().getBlockState().getBaseState().withProperty(BlockStateHelper.FACING_NORMAL.getProperty(), facing), 2);
        notifyNeighborsOfChange();
    }

}
