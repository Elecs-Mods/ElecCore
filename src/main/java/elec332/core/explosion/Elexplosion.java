package elec332.core.explosion;

import elec332.core.world.WorldHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

/**
 * Created by Elec332 on 13-8-2015.
 * <p>
 * Default implementation of {@link AbstractExplosion}
 */
public class Elexplosion extends AbstractExplosion {

    public Elexplosion(Level world, Entity entity, double x, double y, double z, float size) {
        super(world, entity, x, y, z, size);
    }

    @Override
    protected void preExplode() {
        damageEntities(getRadius() * 1.5f, getRadius());
    }

    @Override
    protected void doExplode() {
        if (!this.getWorld().isClientSide()) {
            for (int x = (int) -this.getRadius(); x < this.getRadius(); x++) {
                for (int y = (int) -this.getRadius(); y < this.getRadius(); y++) {
                    for (int z = (int) -this.getRadius(); z < this.getRadius(); z++) {
                        BlockPos targetPosition = this.getLocation().offset(x, y, z);
                        double dist = Math.sqrt(getLocation().distSqr(targetPosition));
                        if (dist < this.getRadius()) {
                            BlockState state = WorldHelper.getBlockState(getWorld(), targetPosition);
                            if (!state.isAir() && state.getExplosionResistance(getWorld(), targetPosition.immutable(), this) >= 0) {
                                if (dist < this.getRadius() - 1 || getWorld().random.nextFloat() > 0.7) {
                                    getWorld().setBlock(targetPosition, Blocks.AIR.defaultBlockState(), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void postExplode() {

    }

}
