package elec332.core.util.math;

import com.google.common.collect.ImmutableList;
import elec332.core.util.ObjectReference;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Elec332 on 2-2-2019
 */
public class HitboxHelper {

    public static boolean collides(VoxelShape shape1, VoxelShape shape2) {
        return VoxelShapes.compare(shape1, shape2, IBooleanFunction.AND);
    }

    public static boolean doesShapeContain(VoxelShape shape, Vector3d pos) {
        return doesShapeContain(shape, pos.x, pos.y, pos.z);
    }

    public static boolean doesShapeContain(VoxelShape shape, double x, double y, double z) {
        return shape.contains(x, y, z);
    }

    public static VoxelShape combineShapes(VoxelShape... shapes) {
        return combineShapes(Arrays.stream(shapes));
    }

    public static VoxelShape combineShapes(Stream<VoxelShape> shapes) {
        return combineShapes(shapes.collect(Collectors.toList()));
    }

    public static VoxelShape combineShapes(Collection<VoxelShape> shapes) {
        VoxelShape ret = shapes.stream().map(HitboxHelper::unwrap).reduce(VoxelShapes.empty(), VoxelShapes::or);
        if (shapes.stream().noneMatch(s -> s instanceof CustomRayTraceVoxelShape)) {
            return ret;
        }
        return new CombinedIndexedVoxelShape(ret, ImmutableList.copyOf(shapes));
    }

    public static VoxelShape rotateFromDown(VoxelShape shape, final Direction facing) {
        final ObjectReference<VoxelShape> shapeRef = ObjectReference.of(VoxelShapes.empty());
        shape.forEachBox((x1, y1, z1, x2, y2, z2) -> shapeRef.set(VoxelShapes.or(shapeRef.get(), VoxelShapes.create(HitboxHelper.rotateFromDown(new AxisAlignedBB(x1, y1, z1, x2, y2, z2), facing)))));
        return shapeRef.get();
    }

    @SuppressWarnings("all")
    public static AxisAlignedBB rotateFromDown(AxisAlignedBB aabb, Direction facing) {
        switch (facing) {
            case UP:
                return new AxisAlignedBB(aabb.minX, 1 - aabb.minY, 1 - aabb.minZ, aabb.maxX, 1 - aabb.maxY, 1 - aabb.maxZ);
            case NORTH:
                return new AxisAlignedBB(aabb.minX, 1 - aabb.minZ, aabb.minY, aabb.maxX, 1 - aabb.maxZ, aabb.maxY);
            case EAST:
                return new AxisAlignedBB(1 - aabb.minY, aabb.minX, aabb.minZ, 1 - aabb.maxY, aabb.maxX, aabb.maxZ);
            case SOUTH:
                return new AxisAlignedBB(aabb.minX, aabb.minZ, 1 - aabb.minY, aabb.maxX, aabb.maxZ, 1 - aabb.maxY);
            case WEST:
                return new AxisAlignedBB(aabb.minY, 1 - aabb.minX, aabb.minZ, aabb.maxY, 1 - aabb.maxX, aabb.maxZ);
            default:
                return aabb;
        }
    }

    private static VoxelShape unwrap(VoxelShape shape) {
        return shape instanceof CustomRayTraceVoxelShape ? ((CustomRayTraceVoxelShape) shape).shape : shape;
    }

}
