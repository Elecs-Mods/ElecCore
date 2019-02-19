package elec332.core.util;

import com.google.common.base.Preconditions;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/**
 * Created by Elec332 on 13-2-2019
 */
public class VectorHelper {

    public static Vec3d addTo(Vec3d v1, Vec3i v2) {
        return new Vec3d(v1.x + v2.getX(), v1.y + v2.getY(), v1.z + v2.getZ());
    }

    public static Vec3d subtractFrom(Vec3d v1, Vec3i v2) {
        return new Vec3d(v1.x - v2.getX(), v1.y - v2.getY(), v1.z - v2.getZ());
    }

    public static Vec3d multiplyVectors(Vec3d v1, Vec3d v2) {
        return new Vec3d(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
    }

    private static final Vec3d[] zeroOnAxisS;
    private static final Vec3d[] oneOnAxisS;

    public static Vec3d zeroOnAxis(EnumFacing side) {
        return zeroOnAxis(Preconditions.checkNotNull(side).getAxis());
    }

    public static Vec3d zeroOnAxis(EnumFacing.Axis axis) {
        return zeroOnAxisS[Preconditions.checkNotNull(axis).ordinal()];
    }

    public static Vec3d oneOnAxis(EnumFacing side) {
        return oneOnAxis(Preconditions.checkNotNull(side).getAxis());
    }

    public static Vec3d oneOnAxis(EnumFacing.Axis axis) {
        return oneOnAxisS[Preconditions.checkNotNull(axis).ordinal()];
    }

    static {
        int max = EnumFacing.Axis.values().length;
        zeroOnAxisS = new Vec3d[max];
        oneOnAxisS = new Vec3d[max];
        for (int i = 0; i < max; i++) {
            EnumFacing.Axis axis = EnumFacing.Axis.values()[i];
            zeroOnAxisS[i] = new Vec3d(axis == EnumFacing.Axis.X ? 0 : 1, axis == EnumFacing.Axis.Y ? 0 : 1, axis == EnumFacing.Axis.Z ? 0 : 1);
            oneOnAxisS[i] = new Vec3d(axis == EnumFacing.Axis.X ? 1 : 0, axis == EnumFacing.Axis.Y ? 1 : 0, axis == EnumFacing.Axis.Z ? 1 : 0);

        }
    }

}
