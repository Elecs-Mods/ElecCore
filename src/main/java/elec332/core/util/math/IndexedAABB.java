package elec332.core.util.math;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class IndexedAABB extends AABB {

    public IndexedAABB(AABB aabb, int index) {
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

    public IndexedAABB(BoundingBox mutableBoundingBox, int index) {
        this(AABB.of(mutableBoundingBox), index);
    }

    public IndexedAABB(Vec3 min, Vec3 max, int index) {
        super(min, max);
        this.index = index;
    }

    public final int index;

    @Override
    public AABB setMinX(double pMinX) {
        return new IndexedAABB(super.setMinX(pMinX), index);
    }

    @Override
    public AABB setMinY(double pMinY) {
        return new IndexedAABB(super.setMinY(pMinY), index);
    }

    @Override
    public AABB setMinZ(double pMinZ) {
        return new IndexedAABB(super.setMinZ(pMinZ), index);
    }

    @Override
    public AABB setMaxX(double pMaxX) {
        return new IndexedAABB(super.setMaxX(pMaxX), index);
    }

    @Override
    public AABB setMaxY(double pMaxY) {
        return new IndexedAABB(super.setMaxY(pMaxY), index);
    }

    @Override
    public AABB setMaxZ(double pMaxZ) {
        return new IndexedAABB(super.setMaxZ(pMaxZ), index);
    }

    @Override
    @Nonnull
    public IndexedAABB contract(double x, double y, double z) {
        return new IndexedAABB(super.contract(x, y, z), index);
    }

    @Override
    public AABB expandTowards(Vec3 pVector) {
        return new IndexedAABB(super.expandTowards(pVector), index);
    }

    @Override
    @Nonnull
    public IndexedAABB expandTowards(double x, double y, double z) {
        return new IndexedAABB(super.expandTowards(x, y, z), index);
    }

    @Override
    @Nonnull
    public IndexedAABB inflate(double value) {
        return new IndexedAABB(super.inflate(value), index);
    }

    @Override
    @Nonnull
    public IndexedAABB inflate(double x, double y, double z) {
        return new IndexedAABB(super.inflate(x, y, z), index);
    }

    @Override
    @Nonnull
    public IndexedAABB intersect(AABB other) {
        return new IndexedAABB(super.intersect(other), index);
    }

    @Override
    @Nonnull
    public IndexedAABB minmax(AABB other) {
        return new IndexedAABB(super.minmax(other), index);
    }

    @Override
    @Nonnull
    public IndexedAABB move(Vec3 vec) {
        return new IndexedAABB(super.move(vec), index);
    }

    @Override
    @Nonnull
    public IndexedAABB move(BlockPos pos) {
        return new IndexedAABB(super.move(pos), index);
    }

    @Override
    @Nonnull
    public IndexedAABB move(double x, double y, double z) {
        return new IndexedAABB(super.move(x, y, z), index);
    }

    @Override
    @Nonnull
    public IndexedAABB deflate(double value) {
        return new IndexedAABB(super.deflate(value), index);
    }

    /*

    Static now, ffs

    @Nullable
    @Override
    public RayTraceResult calculateIntercept(@Nonnull Vec3d vecA, @Nonnull Vec3d vecB, @Nullable BlockPos pos) {
        RayTraceResult ret = super.calculateIntercept(vecA, vecB, pos);
        if (ret != null) {
            ret.subHit = index;
        }
        return ret;
    }
    */

    @Override
    public String toString() {
        return super.toString() + " @ " + index;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && (!(o instanceof IndexedAABB) || ((IndexedAABB) o).index == index);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + index * 31;
    }

}
