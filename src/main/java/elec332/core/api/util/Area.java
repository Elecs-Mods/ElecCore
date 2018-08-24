package elec332.core.api.util;

import net.minecraft.util.math.BlockPos;

/*
 * This class was originally created by Lumaceon.
 * You can find it here: https://github.com/Lumaceon/ClockworkPhase2/blob/master/src/main/java/lumaceon/mods/clockworkphase2/util/Area.java
 */
public class Area {

    public Area(BlockPos pos1, BlockPos pos2) {
        this(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    public Area(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public final int minX, minY, minZ, maxX, maxY, maxZ;

    public short getWidth() {
        return (short) (maxX - minX);
    }

    public short getHeight() {
        return (short) (maxY - minY);
    }

    public short getLength() {
        return (short) (maxZ - minZ);
    }

    public short getBlockWidth() {
        return (short) (getWidth() + 1);
    }

    public short getBlockHeight() {
        return (short) (getHeight() + 1);
    }

    public short getBlockLength() {
        return (short) (getLength() + 1);
    }

    public int getBlockCount() {
        return getBlockWidth() * getBlockHeight() * getBlockLength();
    }

    public Area offset(BlockPos pos) {
        return offset(pos.getX(), pos.getY(), pos.getZ());
    }

    public Area offset(int x, int y, int z) {
        return new Area(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z);
    }

    public boolean doAreasIntersect(Area otherArea) {
        return intersects(otherArea.minX, otherArea.minY, otherArea.minZ, otherArea.maxX, otherArea.maxY, otherArea.maxZ);
    }

    public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
        return this.minX < Math.max(x1, x2) && this.maxX > Math.min(x1, x2) && this.minY < Math.max(y1, y2) && this.maxY > Math.min(y1, y2) && this.minZ < Math.max(z1, z2) && this.maxZ > Math.min(z1, z2);
    }

    @Override
    public String toString() {
        return "Area{" +
                "x1=" + minX +
                ", y1=" + minY +
                ", z1=" + minZ +
                ", x2=" + maxX +
                ", y2=" + maxY +
                ", z2=" + maxZ +
                '}';
    }

}
