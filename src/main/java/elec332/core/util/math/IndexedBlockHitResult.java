package elec332.core.util.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class IndexedBlockHitResult extends BlockHitResult {

    public IndexedBlockHitResult(Vec3 pLocation, Direction pDirection, BlockPos pBlockPos, boolean pInside, int subHit, Object hitInfo) {
        super(pLocation, pDirection, pBlockPos, pInside);
        this.subHit = subHit;
        this.hitInfo = hitInfo;
    }

    public IndexedBlockHitResult(BlockHitResult bhr, int subHit, Object hitInfo) {
        super(bhr.getLocation(), bhr.getDirection(), bhr.getBlockPos(), bhr.isInside());
        this.subHit = subHit;
        this.hitInfo = hitInfo;
    }

    public final int subHit;
    public final Object hitInfo;

    @Override
    public BlockHitResult withDirection(Direction pNewFace) {
        return new IndexedBlockHitResult(super.withDirection(pNewFace), this.subHit, this.hitInfo);
    }

    @Override
    public BlockHitResult withPosition(BlockPos pPos) {
        return new IndexedBlockHitResult(super.withPosition(pPos), this.subHit, this.hitInfo);
    }

}
