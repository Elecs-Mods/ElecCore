package elec332.core.multiblock;

import elec332.core.compat.handlers.WailaCompatHandler;
import elec332.core.main.ElecCore;
import elec332.core.tile.IInventoryTile;
import elec332.core.tile.TileBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

/**
 * Created by Elec332 on 28-7-2015.
 */
public abstract class AbstractMultiBlockTile extends TileBase implements IMultiBlockTile, IInventoryTile, WailaCompatHandler.IWailaInfoTile{

    public AbstractMultiBlockTile(MultiBlockRegistry registry){
        super();
        this.multiBlockData = new MultiBlockData(this, registry);
    }

    private MultiBlockData multiBlockData;

    @Override
    @SuppressWarnings("deprecation")
    public boolean canUpdate() {
        return false;
    }

    @Override
    public final boolean onBlockActivated(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        return getMultiBlock() == null ? onBlockActivatedBy(player, side, hitX, hitY, hitZ) : getMultiBlock().onAnyBlockActivated(player);
    }

    public boolean onBlockActivatedBy(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        multiBlockData.writeToNBT(tagCompound);
        if (getMultiBlock() != null){
            if (getMultiBlock().isSaveDelegate(this))
                getMultiBlock().writeToNBT(tagCompound);
        }
    }

    @Override
    public void readFromNBT(final NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        multiBlockData.readFromNBT(tagCompound);
        ElecCore.tickHandler.registerCall(new Runnable() {
            @Override
            public void run() {
                if (getMultiBlock() != null) {
                    if (getMultiBlock().isSaveDelegate(AbstractMultiBlockTile.this))
                        getMultiBlock().readFromNBT(tagCompound);
                }
            }
        }, FMLCommonHandler.instance().getEffectiveSide());
    }

    /**
     * An IMultiblock cannot be saved to NBT, every time this tile gets loaded after the tile unloaded, this gets re-assigned
     *
     * @param multiBlock The multiblock in which the tile belongs
     * @param facing     The facing of the multiblock -Save this value to NBT!
     * @param structure  The identifier of the multiblock-structure -Save this value to NBT aswell!
     */
    @Override
    public void setMultiBlock(IMultiBlock multiBlock, EnumFacing facing, String structure) {
        multiBlockData.setMultiBlock(multiBlock, facing, structure);
        onMultiBlockCreated();
    }

    /**
     * When an multiblock becomes invalid, this method will get called, use it
     * to set the multiblock to null, and make sure that #isValidMultiBlock() returns false after this method finished processing!
     */
    @Override
    public void invalidateMultiBlock() {
        multiBlockData.invalidateMultiBlock();
        onMultiBlockRemoved();
    }

    /**
     * This returns if this tile is actually part of an multiblock or not
     *
     * @return weather the multiblock is valid, make sure to read/write the value from/to NBT!
     */
    @Override
    public boolean isValidMultiBlock() {
        return multiBlockData.isValidMultiBlock();
    }

    /**
     * This is used for saving, just like TileEntities, make sure this returns the name from the @link IMultiBlockStructure
     * HINT: Save this value to NBT aswell;
     *
     * @return the name of the structure
     */
    @Override
    public String getStructureIdentifier() {
        return multiBlockData.getStructureIdentifier();
    }

    /**
     * This value should return the same value as the value in #setMultiBlock
     *
     * @return The facing of the multiblock
     */
    @Override
    public EnumFacing getMultiBlockFacing() {
        return multiBlockData.getFacing();
    }

    /**
     * Returns the multiblock this tile belongs too, can be null
     *
     * @return said multiblock
     */
    @Override
    public AbstractMultiBlock getMultiBlock() {
        return (AbstractMultiBlock) multiBlockData.getMultiBlock();
    }

    protected void onMultiBlockCreated(){
    }

    protected void onMultiBlockRemoved(){
    }

    @Override
    public void validate() {
        super.validate();
        multiBlockData.tileEntityValidate();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        multiBlockData.tileEntityChunkUnload();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        multiBlockData.tileEntityInvalidate();
    }

    @Override
    public Container getGuiServer(EntityPlayer player) {
        return getMultiBlock() == null ? null : getMultiBlock().getGuiServer(player);
    }

    @Override
    public Object getGuiClient(EntityPlayer player) {
        return getMultiBlock() == null ? null : getMultiBlock().getGuiClient(player);
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (getMultiBlock() != null)
            return getMultiBlock().getWailaBody(itemStack, currentTip, accessor, config);
        return currentTip;
    }

    @Override
    public NBTTagCompound getWailaTag(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos){
        if (getMultiBlock() != null)
            return getMultiBlock().getWailaTag(player, te, tag, world, pos);
        return tag;
    }


}
