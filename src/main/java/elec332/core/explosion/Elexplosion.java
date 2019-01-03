package elec332.core.explosion;

import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 13-8-2015.
 * <p>
 * Default implementation of {@link AbstractExplosion}
 */
public class Elexplosion extends AbstractExplosion {

    public Elexplosion(World world, Entity entity, double x, double y, double z, float size) {
        super(world, entity, x, y, z, size);
    }

    @Override
    protected void preExplode() {
        damageEntities(getRadius() * 1.5f, getRadius());
    }

    @Override
    protected void doExplode() {
        if (!this.getWorld().isRemote) {
            for (int x = (int) -this.getRadius(); x < this.getRadius(); x++) {
                for (int y = (int) -this.getRadius(); y < this.getRadius(); y++) {
                    for (int z = (int) -this.getRadius(); z < this.getRadius(); z++) {
                        BlockPos targetPosition = this.getLocation().add(x, y, z);
                        double dist = Math.sqrt(getLocation().distanceSq(targetPosition));
                        if (dist < this.getRadius()) {
                            IBlockState state = WorldHelper.getBlockState(getWorld(), targetPosition);
                            Block block = state.getBlock();
                            if (block != null && !block.isAir(state, getWorld(), targetPosition) && state.getBlockHardness(getWorld(), targetPosition.toImmutable()) >= 0) {
                                if (dist < this.getRadius() - 1 || getWorld().rand.nextFloat() > 0.7) {
                                    getWorld().setBlockState(targetPosition, Blocks.AIR.getDefaultState(), 3);
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
