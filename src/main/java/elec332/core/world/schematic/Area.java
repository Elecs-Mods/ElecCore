package elec332.core.world.schematic;

/*
 * This class was made by Lumaceon.
 * You can find it here: https://github.com/Lumaceon/ClockworkPhase2/blob/master/src/main/java/lumaceon/mods/clockworkphase2/util/Area.java
 */

import net.minecraft.util.math.BlockPos;

public class Area {

    public Area(BlockPos pos1, BlockPos pos2){
        this(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    public Area(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public final int x1, y1, z1, x2, y2, z2;

    public short getWidth() {
        return (short) (Math.abs(x1 - x2) + 1);
    }

    public short getHeight() {
        return (short) (Math.abs(y1 - y2) + 1);
    }

    public short getLength() {
        return (short) (Math.abs(z1 - z2) + 1);
    }

    public int getBlockCount() {
        return getWidth() * getHeight() * getLength();
    }

    public boolean doAreasIntersect(Area otherArea) {
        return !(this.x2 < otherArea.x1)
                && !(otherArea.x2 < this.x1)
                && !(this.y2 < otherArea.y1)
                && !(otherArea.y2 < this.y1)
                && !(this.z2 < otherArea.z1)
                && !(otherArea.z2 < this.z1);
    }

    @Override
    public String toString() {
        return "Area{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", z1=" + z1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", z2=" + z2 +
                '}';
    }
}
