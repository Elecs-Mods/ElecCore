package elec332.core.util.math;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

/**
 * Created by Elec332 on 13-2-2019
 */
public class VectorHelper {

    public static Vec3 addTo(Vec3 v1, Vec3i v2) {
        return new Vec3(v1.x + v2.getX(), v1.y + v2.getY(), v1.z + v2.getZ());
    }

    public static Vec3 subtractFrom(Vec3 v1, Vec3i v2) {
        return new Vec3(v1.x - v2.getX(), v1.y - v2.getY(), v1.z - v2.getZ());
    }

    public static Vec3 multiplyVectors(Vec3 v1, Vec3 v2) {
        return new Vec3(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
    }

    private static final Vec3[] zeroOnAxisS;
    private static final Vec3[] oneOnAxisS;

    public static Vec3 zeroOnAxis(Direction side) {
        return zeroOnAxis(Preconditions.checkNotNull(side).getAxis());
    }

    public static Vec3 zeroOnAxis(Direction.Axis axis) {
        return zeroOnAxisS[Preconditions.checkNotNull(axis).ordinal()];
    }

    public static Vec3 oneOnAxis(Direction side) {
        return oneOnAxis(Preconditions.checkNotNull(side).getAxis());
    }

    public static Vec3 oneOnAxis(Direction.Axis axis) {
        return oneOnAxisS[Preconditions.checkNotNull(axis).ordinal()];
    }

    static {
        int max = Direction.Axis.values().length;
        zeroOnAxisS = new Vec3[max];
        oneOnAxisS = new Vec3[max];
        for (int i = 0; i < max; i++) {
            Direction.Axis axis = Direction.Axis.values()[i];
            zeroOnAxisS[i] = new Vec3(axis == Direction.Axis.X ? 0 : 1, axis == Direction.Axis.Y ? 0 : 1, axis == Direction.Axis.Z ? 0 : 1);
            oneOnAxisS[i] = new Vec3(axis == Direction.Axis.X ? 1 : 0, axis == Direction.Axis.Y ? 1 : 0, axis == Direction.Axis.Z ? 1 : 0);

        }
    }

}
