package elec332.core.util.math;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;

public class IndexedAABB extends AxisAlignedBB {

    public IndexedAABB(AxisAlignedBB aabb, int index) {
        super(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
        this.index = index;
    }

    public IndexedAABB(double x1, double y1, double z1, double x2, double y2, double z2, int index) {
        super(x1, y1, z1, x2, y2, z2);
        this.index = index;
    }

    public IndexedAABB(BlockPos pos, int index) {
        super(pos);
        this.index = index;
    }

    public IndexedAABB(BlockPos pos1, BlockPos pos2, int index) {
        super(pos1, pos2);
        this.index = index;
    }

    public IndexedAABB(MutableBoundingBox mutableBoundingBox, int index) {
        this(AxisAlignedBB.toImmutable(mutableBoundingBox), index);
    }

    public IndexedAABB(Vector3d min, Vector3d max, int index) {
        super(min, max);
        this.index = index;
    }

    public final int index;

    @Override
    @Nonnull
    public IndexedAABB contract(double x, double y, double z) {
        return new IndexedAABB(super.contract(x, y, z), index);
    }

    @Override
    @Nonnull
    public IndexedAABB expand(double x, double y, double z) {
        return new IndexedAABB(super.expand(x, y, z), index);
    }

    @Override
    @Nonnull
    public IndexedAABB grow(double value) {
        return new IndexedAABB(super.grow(value), index);
    }

    @Override
    @Nonnull
    public IndexedAABB grow(double x, double y, double z) {
        return new IndexedAABB(super.grow(x, y, z), index);
    }

    @Override
    @Nonnull
    public IndexedAABB intersect(AxisAlignedBB other) {
        return new IndexedAABB(super.intersect(other), index);
    }

    @Override
    @Nonnull
    public IndexedAABB union(AxisAlignedBB other) {
        return new IndexedAABB(super.union(other), index);
    }

    @Override
    @Nonnull
    public IndexedAABB offset(Vector3d vec) {
        return new IndexedAABB(super.offset(vec), index);
    }

    @Override
    @Nonnull
    public IndexedAABB offset(BlockPos pos) {
        return new IndexedAABB(super.offset(pos), index);
    }

    @Override
    @Nonnull
    public IndexedAABB offset(double x, double y, double z) {
        return new IndexedAABB(super.offset(x, y, z), index);
    }

    @Override
    public boolean intersects(Vector3d min, Vector3d max) {
        return this.intersects(Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z), Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));
    }

    @Override
    @Nonnull
    public IndexedAABB shrink(double value) {
        return new IndexedAABB(super.shrink(value), index);
    }

    /*

    Static now, ffs

    @Nullable
    @Override
    public RayTraceResult calculateIntercept(@Nonnull Vector3d vecA, @Nonnull Vector3d vecB, @Nullable BlockPos pos) {
        RayTraceResult ret = super.calculateIntercept(vecA, vecB, pos);
        if (ret != null) {
            ret.subHit = index;
        }
        return ret;
    }
    */

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && (!(o instanceof IndexedAABB) || ((IndexedAABB) o).index == index);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + index * 31;
    }

}
