package elec332.core.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Created by Elec332 on 2-2-2019
 */
public class HitboxHelper {

    public static boolean doesShapeContain(VoxelShape shape, Vec3d pos) {
        return doesShapeContain(shape, pos.x, pos.y, pos.z);
    }

    public static boolean doesShapeContain(VoxelShape shape, double x, double y, double z) {
        return shape.contains(x, y, z);
    }

    public static VoxelShape combineShapes(VoxelShape... shapes) {
        return combineShapes(Arrays.stream(shapes));
    }

    public static VoxelShape combineShapes(Collection<VoxelShape> shapes) {
        return combineShapes(shapes.stream());
    }

    public static VoxelShape combineShapes(Stream<VoxelShape> shapes) {
        return shapes.reduce(VoxelShapes.empty(), VoxelShapes::or);
    }

    public static VoxelShape rotateFromDown(VoxelShape shape, final EnumFacing facing) {
        final ObjectReference<VoxelShape> shapeRef = ObjectReference.of(VoxelShapes.empty());
        shape.forEachBox((x1, y1, z1, x2, y2, z2) -> shapeRef.set(VoxelShapes.or(shapeRef.get(), VoxelShapes.create(HitboxHelper.rotateFromDown(new AxisAlignedBB(x1, y1, z1, x2, y2, z2), facing)))));
        return shapeRef.get();
    }

    @SuppressWarnings("all")
    public static AxisAlignedBB rotateFromDown(AxisAlignedBB aabb, EnumFacing facing) {
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

}
