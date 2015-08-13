package elec332.core.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 23-4-2015.
 */
public class BlockLoc {

    public BlockLoc(TileEntity tileEntity){
        xCoord = tileEntity.xCoord;
        yCoord = tileEntity.yCoord;
        zCoord = tileEntity.zCoord;
    }

    public BlockLoc(int xCoord, int yCoord, int zCoord){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
    }

    public BlockLoc atSide(ForgeDirection direction){
        return new BlockLoc(xCoord+direction.offsetX, yCoord+direction.offsetY, zCoord+direction.offsetZ);
    }

    public int xCoord;
    public int yCoord;
    public int zCoord;

    public BlockLoc copy(){
        return new BlockLoc(xCoord, yCoord, zCoord);
    }

    public BlockLoc add(float i){
        this.xCoord += i;
        this.yCoord += i;
        this.zCoord += i;
        return this;
    }

    public BlockLoc translate(BlockLoc blockLoc){
        xCoord += blockLoc.xCoord;
        yCoord += blockLoc.yCoord;
        zCoord += blockLoc.zCoord;
        return this;
    }

    public double distanceSquared(BlockLoc loc){
        double dx = loc.xCoord - this.xCoord;
        double dy = loc.yCoord - this.yCoord;
        double dz = loc.zCoord - this.zCoord;
        return dx * dx + dy * dy + dz * dz;
    }

    public double distance(BlockLoc loc){
        return Math.sqrt(distanceSquared(loc));
    }

    public Vec3 toVec3(){
        return Vec3.createVectorHelper(xCoord, yCoord, zCoord);
    }

    public BlockLoc(NBTTagCompound tagCompound){
        this.xCoord = (int)tagCompound.getDouble("xCoord");
        this.yCoord = (int)tagCompound.getDouble("yCoord");
        this.zCoord = (int)tagCompound.getDouble("zCoord");
    }

    public NBTTagCompound toNBT(NBTTagCompound tagCompound){
        tagCompound.setDouble("xCoord", xCoord);
        tagCompound.setDouble("yCoord", yCoord);
        tagCompound.setDouble("zCoord", zCoord);
        return tagCompound;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockLoc && ((BlockLoc)obj).zCoord == zCoord && ((BlockLoc)obj).yCoord == yCoord && ((BlockLoc)obj).xCoord == xCoord;
    }

    @Override
    public String toString() {
        return "("+xCoord+","+yCoord+","+zCoord+")";
    }

    @Override
    public int hashCode() {
        return (xCoord+yCoord+zCoord);
    }
}
