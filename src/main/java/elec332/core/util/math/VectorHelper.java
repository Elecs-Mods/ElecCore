package elec332.core.util.math;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/**
 * Created by Elec332 on 13-2-2019
 */
public class VectorHelper {

    public static Vector3f copyVector(Vector3f v3f) {
        return new Vector3f(v3f.getX(), v3f.getY(), v3f.getZ());
    }

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

    public static Vec3d zeroOnAxis(Direction side) {
        return zeroOnAxis(Preconditions.checkNotNull(side).getAxis());
    }

    public static Vec3d zeroOnAxis(Direction.Axis axis) {
        return zeroOnAxisS[Preconditions.checkNotNull(axis).ordinal()];
    }

    public static Vec3d oneOnAxis(Direction side) {
        return oneOnAxis(Preconditions.checkNotNull(side).getAxis());
    }

    public static Vec3d oneOnAxis(Direction.Axis axis) {
        return oneOnAxisS[Preconditions.checkNotNull(axis).ordinal()];
    }

    static {
        int max = Direction.Axis.values().length;
        zeroOnAxisS = new Vec3d[max];
        oneOnAxisS = new Vec3d[max];
        for (int i = 0; i < max; i++) {
            Direction.Axis axis = Direction.Axis.values()[i];
            zeroOnAxisS[i] = new Vec3d(axis == Direction.Axis.X ? 0 : 1, axis == Direction.Axis.Y ? 0 : 1, axis == Direction.Axis.Z ? 0 : 1);
            oneOnAxisS[i] = new Vec3d(axis == Direction.Axis.X ? 1 : 0, axis == Direction.Axis.Y ? 1 : 0, axis == Direction.Axis.Z ? 1 : 0);

        }
    }

}
