package elec332.core.util.math;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    public BlockHitResult clip(@Nonnull Vec3 start, @Nonnull Vec3 end, @Nonnull BlockPos pos) {
        BlockHitResult hit = shape.clip(start, end, pos);
        if (hit == null) {
            return null;
        }
        return new IndexedBlockHitResult(hit, subHit, hitInfo);
    }

}