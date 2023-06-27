package elec332.core.util.math;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 28-12-2019
 * <p>
 * Originally created by Amadornes
 */
public class IndexedVoxelShape extends CustomRayTraceVoxelShape {

    public IndexedVoxelShape(VoxelShape shape, int subHit, Object hitInfo) {
        super(shape);
        this.subHit = subHit;
        this.hitInfo = hitInfo;
    }

    private final int subHit;
    private final Object hitInfo;

    @Override
    @Nullable
    public BlockRayTraceResult rayTrace(@Nonnull Vector3d start, @Nonnull Vector3d end, @Nonnull BlockPos pos) {
        BlockRayTraceResult hit = shape.rayTrace(start, end, pos);
        if (hit == null) {
            return null;
        }
        hit.subHit = subHit;
        hit.hitInfo = hitInfo;
        return hit;
    }

}