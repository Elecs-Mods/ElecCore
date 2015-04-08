package elec332.core.baseclasses.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

/**
 * Created by Elec332 on 8-4-2015.
 */
public class TileBase extends TileEntity {

    public void notifyNeighboursOfDataChange(){
        this.markDirty();
        this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType);
    }

    public Vec3 myLocation(){
        return Vec3.createVectorHelper(this.xCoord, this.yCoord, this.zCoord);
    }

    public boolean timeCheck() {
        return this.worldObj.getTotalWorldTime() % 32L == 0L;
    }

    protected void setBlockMetadataWithNotify(int meta){
        this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 2);
        notifyNeighboursOfDataChange();
    }
}
