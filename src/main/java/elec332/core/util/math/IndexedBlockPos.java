package elec332.core.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/**
 * Created by Elec332 on 5-2-2019
 */
public class IndexedBlockPos extends BlockPos {

    public IndexedBlockPos(int x, int y, int z, int index) {
        super(x, y, z);
        this.index = index;
    }

    public IndexedBlockPos(double x, double y, double z, int index) {
        super(x, y, z);
        this.index = index;
    }

    public IndexedBlockPos(Entity source, int index) {
        super(source);
        this.index = index;
    }

    public IndexedBlockPos(Vec3d vec, int index) {
        super(vec);
        this.index = index;
    }

    public IndexedBlockPos(Vec3i source, int index) {
        super(source);
        this.index = index;
    }

    public final int index;

}
