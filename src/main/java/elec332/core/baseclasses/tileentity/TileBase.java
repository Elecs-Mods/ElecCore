package elec332.core.baseclasses.tileentity;

import elec332.core.main.ElecCore;
import elec332.core.util.BlockLoc;
import elec332.core.util.IRunOnce;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Elec332 on 8-4-2015.
 */
public class TileBase extends TileEntity {

    @Override
    public void validate() {
        super.validate();
        ElecCore.tickHandler.registerCall(new IRunOnce() {
            @Override
            public void run() {
                if (getWorldObj().blockExists(xCoord, yCoord, zCoord)) {
                    onTileLoaded();
                }
            }
        });
    }


    public void invalidate() {
        if (!isInvalid()){
            super.invalidate();
            onTileUnloaded();
        }
    }

    public void onChunkUnload() {
        if (!isInvalid()){
            super.onChunkUnload();
            super.invalidate();
            onTileUnloaded();
        }
    }


    public void onTileLoaded(){
    }

    public void onTileUnloaded(){
    }

    public void notifyNeighboursOfDataChange(){
        this.markDirty();
        this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType);
    }

    public BlockLoc myLocation(){
        return new BlockLoc(this.xCoord, this.yCoord, this.zCoord);
    }

    public boolean timeCheck() {
        return this.worldObj.getTotalWorldTime() % 32L == 0L;
    }

    protected void setBlockMetadataWithNotify(int meta){
        this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 2);
        notifyNeighboursOfDataChange();
    }
}
