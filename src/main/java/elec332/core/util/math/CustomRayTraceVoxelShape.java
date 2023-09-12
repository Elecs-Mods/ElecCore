package elec332.core.util.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Created by Elec332 on 28-12-2019
 * <p>
 * Originally created by Amadornes
 */
public abstract class CustomRayTraceVoxelShape extends SliceShape {

    public CustomRayTraceVoxelShape(VoxelShape shape) {
        super(shape, Direction.Axis.Y, 0);
        this.shape = shape;
    }

    protected final VoxelShape shape;

    @Override
    public double min(@Nonnull Direction.Axis axis) {
        return shape.min(axis);
    }

    @Override
    public double max(@Nonnull Direction.Axis axis) {
        return shape.max(axis);
    }

    @Nonnull
    @Override
    public AABB bounds() {
        return shape.bounds();
    }

    @Override
    public boolean isEmpty() {
        return shape.isEmpty();
    }

    @Nonnull
    @Override
    public VoxelShape move(double x, double y, double z) {
        return shape.move(x, y, z);
    }

    @Nonnull
    @Override
    public VoxelShape optimize() {
        return shape.optimize();
    }

    @Override
    public void forAllEdges(Shapes.DoubleLineConsumer lineConsumer) {
        shape.forAllEdges(lineConsumer);
    }

    @Override
    public void forAllBoxes(Shapes.DoubleLineConsumer lineConsumer) {
        shape.forAllBoxes(lineConsumer);
    }

    @Nonnull
    @Override
    public List<AABB> toAabbs() {
        return shape.toAabbs();
    }

    @Override
    public double min(@Nonnull Direction.Axis p_197764_1_, double p_197764_2_, double p_197764_4_) {
        return shape.min(p_197764_1_, p_197764_2_, p_197764_4_);
    }

    @Override
    public double max(@Nonnull Direction.Axis p_197760_1_, double p_197760_2_, double p_197760_4_) {
        return shape.max(p_197760_1_, p_197760_2_, p_197760_4_);
    }

    @Nullable
    @Override
    public abstract BlockHitResult clip(@Nonnull Vec3 pStartVec,@Nonnull  Vec3 pEndVec,@Nonnull  BlockPos pPos);

    @Override
    public Optional<Vec3> closestPointTo(Vec3 pPoint) {
        return shape.closestPointTo(pPoint);
    }

    @Override
    public VoxelShape getFaceShape(Direction pSide) {
        return shape.getFaceShape(pSide);
    }

    @Override
    public double collide(Direction.Axis pMovementAxis, AABB pCollisionBox, double pDesiredOffset) {
        return shape.collide(pMovementAxis, pCollisionBox, pDesiredOffset);
    }

}
