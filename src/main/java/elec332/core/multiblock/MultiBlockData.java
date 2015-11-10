package elec332.core.multiblock;

import com.google.common.base.Strings;
import elec332.core.main.ElecCore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 28-7-2015.
 */
public final class MultiBlockData {

    public MultiBlockData(IMultiBlockTile tile, MultiBlockRegistry registry){
        this.tile = tile;
        this.registry = registry;
    }

    private final MultiBlockRegistry registry;
    private final IMultiBlockTile tile;
    private IMultiBlock multiBlock = null;
    private ForgeDirection mbFacing = null;
    private boolean valid = false;
    private String structureID = "";

    private TileEntity getTileEntity(){
        return (TileEntity) tile;
    }

    private World getTileEntityWorld(){
        return getTileEntity().getWorldObj();
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        String s = mbFacing == null?"":mbFacing.toString();
        tagCompound.setString("facing_MBS", s);
        tagCompound.setBoolean("valid_MBS", valid);
        tagCompound.setString("structure_MBS", structureID);
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
        String s = tagCompound.getString("facing_MBS");
        if (!Strings.isNullOrEmpty(s))
            this.mbFacing = ForgeDirection.valueOf(s);
        this.valid = tagCompound.getBoolean("valid_MBS");
        this.structureID = tagCompound.getString("structure_MBS");
    }

    public void setMultiBlock(IMultiBlock multiBlock, ForgeDirection facing, String structureID) {
        this.multiBlock = multiBlock;
        this.mbFacing = facing;
        this.valid = true;
        this.structureID = structureID;
        getTileEntity().markDirty();
    }

    public void invalidateMultiBlock() {
        this.multiBlock = null;
        this.mbFacing = null;
        this.valid = false;
        this.structureID = "";
        getTileEntity().markDirty();
    }

    public boolean isValidMultiBlock() {
        return valid;
    }

    public String getStructureIdentifier() {
        return structureID;
    }

    public ForgeDirection getFacing() {
        return mbFacing;
    }

    public IMultiBlock getMultiBlock() {
        return multiBlock;
    }

    public void tileEntityValidate(){
        //if (!getTileEntityWorld().isRemote) {
            ElecCore.tickHandler.registerCall(new Runnable() {
                @Override
                public void run() {
                    IMultiBlock.tileEntityValidate(tile, multiBlock, registry);
                }
            }, getTileEntityWorld());
        //}
    }

    public void tileEntityInvalidate(){
        //if (!getTileEntityWorld().isRemote)
            IMultiBlock.tileEntityInvalidate(multiBlock);
    }

    public void tileEntityChunkUnload(){
        //if (!getTileEntityWorld().isRemote)
            IMultiBlock.tileEntityChunkUnload(multiBlock, tile);
    }
}
