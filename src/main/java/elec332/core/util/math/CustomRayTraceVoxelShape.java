package elec332.core.util.math;

import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.SplitVoxelShape;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 28-12-2019
 * <p>
 * Originally created by Amadornes
 */
public abstract class CustomRayTraceVoxelShape extends SplitVoxelShape {

    public CustomRayTraceVoxelShape(VoxelShape shape) {
        super(shape, Direction.Axis.Y, 0);
        this.shape = shape;
    }

    protected final VoxelShape shape;

    @Override
    public double getStart(@Nonnull Direction.Axis axis) {
        return shape.getStart(axis);
    }

    @Override
    public double getEnd(@Nonnull Direction.Axis axis) {
        return shape.getEnd(axis);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox() {
        return shape.getBoundingBox();
    }

    @Override
    public boolean isEmpty() {
        return shape.isEmpty();
    }

    @Nonnull
    @Override
    public VoxelShape withOffset(double x, double y, double z) {
        return shape.withOffset(x, y, z);
    }

    @Nonnull
    @Override
    public VoxelShape simplify() {
        return shape.simplify();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void forEachEdge(@Nonnull VoxelShapes.ILineConsumer lineConsumer) {
        shape.forEachEdge(lineConsumer);
    }

    @Override
    public void forEachBox(@Nonnull VoxelShapes.ILineConsumer lineConsumer) {
        shape.forEachBox(lineConsumer);
    }

    @Nonnull
    @Override
    public List<AxisAlignedBB> toBoundingBoxList() {
        return shape.toBoundingBoxList();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public double max(@Nonnull Direction.Axis p_197760_1_, double p_197760_2_, double p_197760_4_) {
        return shape.max(p_197760_1_, p_197760_2_, p_197760_4_);
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return shape.contains(x, y, z);
    }

    @Override
    @Nullable
    public abstract BlockRayTraceResult rayTrace(@Nonnull Vector3d start, @Nonnull Vector3d end, @Nonnull BlockPos pos);

    @Nonnull
    @Override
    public VoxelShape project(@Nonnull Direction direction) {
        return shape.project(direction);
    }

    @Override
    public double getAllowedOffset(@Nonnull Direction.Axis axis, @Nonnull AxisAlignedBB aabb, double p_212430_3_) {
        return shape.getAllowedOffset(axis, aabb, p_212430_3_);
    }

}
