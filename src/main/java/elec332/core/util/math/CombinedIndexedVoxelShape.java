package elec332.core.util.math;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 28-12-2019
 * <p>
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
    public BlockHitResult clip(@Nonnull Vec3 start, @Nonnull Vec3 end, @Nonnull BlockPos pos) {
        BlockHitResult closest = null;
        double closestDist = Double.POSITIVE_INFINITY;
        for (VoxelShape shape : shapes) {
            BlockHitResult hit = shape.clip(start, end, pos);
            if (hit == null) {
                continue;
            }
            double dist = hit.getLocation().distanceToSqr(start);
            if (closestDist < dist) {
                continue;
            }
            closest = hit;
            closestDist = dist;
        }
        return closest;
    }

}