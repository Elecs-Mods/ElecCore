package elec332.core.util.math;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 28-12-2019
 *
 * Originally created by Amadornes
 */
public class CombinedIndexedVoxelShape extends CustomRayTraceVoxelShape {

    public CombinedIndexedVoxelShape(VoxelShape parent, List<VoxelShape> shapes) {
        super(parent);
        this.shapes = shapes;
    }

    private final List<VoxelShape> shapes;

    @Override
    @Nullable
    public BlockRayTraceResult rayTrace(@Nonnull Vec3d start, @Nonnull Vec3d end, @Nonnull BlockPos pos) {
        BlockRayTraceResult closest = null;
        double closestDist = Double.POSITIVE_INFINITY;
        for (VoxelShape shape : shapes) {
            BlockRayTraceResult hit = shape.rayTrace(start, end, pos);
            if (hit == null) {
                continue;
            }
            double dist = hit.getHitVec().squareDistanceTo(start);
            if (closestDist < dist) {
                continue;
            }
            closest = hit;
            closestDist = dist;
        }
        return closest;
    }

}