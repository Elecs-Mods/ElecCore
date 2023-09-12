package elec332.core.explosion;

import elec332.core.util.EntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Elec332 on 13-8-2015.
 * <p>
 * Less-laggy implementation of an explosion
 */
public abstract class AbstractExplosion extends Explosion {

    public AbstractExplosion(Level world, Entity entity, double x, double y, double z, float size) {
        super(world, entity, x, y, z, size, false, BlockInteraction.DESTROY);
        this.world = world;
        this.location = new BlockPos((int) x, (int) y, (int) z);
    }

    private final Level world;
    private final BlockPos location;

    /**
     * Go boom
     */
    public void explode() {
        ExplosionEvent event = new ExplosionEvent.Start(world, this);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            preExplode();
            doExplode();
            postExplode();
        }
    }

    /**
     * Prepare the explosion
     */
    protected abstract void preExplode();

    /**
     * Explode
     */
    protected abstract void doExplode();

    /**
     * Do something afterwards
     */
    protected abstract void postExplode();

    /**
     * Damages all entities in a certain radius from the center
     * Damage done depends on the power and distance from the center
     *
     * @param radius The radius in which to damage entities
     * @param power  The blast power
     */
    protected void damageEntities(float radius, float power) {
        if (!world.isClientSide()) {
            radius *= 2.0f;
            BlockPos minCoord = new BlockPos(location);
            float minR = -radius - 1;
            float maxR = radius + 1;
            minCoord = minCoord.offset(minR, minR, minR);
            BlockPos maxCoord = new BlockPos(location);
            maxCoord = maxCoord.offset(maxR, maxR, maxR);
            List<LivingEntity> allEntities = world.getEntitiesOfClass(LivingEntity.class, new AABB(minCoord, maxCoord));
            allEntities.sort((Comparator<Entity>) (o1, o2) -> (int) (getDistance(o1) - getDistance(o2)));
            for (Entity entity : allEntities) {
                double distance = getDistance(entity) / radius;
                if (distance <= 1.0D) {
                    double xDifference = entity.getX() - location.getX();
                    double yDifference = entity.getY() - location.getY();
                    double zDifference = entity.getZ() - location.getZ();
                    double d1 = Math.sqrt(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                    xDifference /= d1;
                    yDifference /= d1;
                    zDifference /= d1;
                    Vec3 motionMod = new Vec3(xDifference, yDifference, zDifference);
                    double density = Explosion.getSeenPercent(new Vec3(location.getX(), location.getY(), location.getZ()), entity);
                    double d2 = (1.0D - distance) * density;
                    int damage = (int) ((d2 * d2 + d2) / 2.0D * 8.0D * power + 1.0D);
                    entity.hurt(DamageSource.explosion(this), damage);
                    EntityHelper.addMotion(entity, motionMod.multiply(d2, d2, d2));
                }
            }
        }
    }

    private double getDistance(Entity entity) {
        return Math.sqrt(entity.distanceToSqr(location.getX(), location.getY(), location.getZ()));
    }

    public final float getRadius() {
        return this.radius;
    }


    public final BlockPos getLocation() {
        return location;
    }

    public final Level getWorld() {
        return world;
    }

    @Override
    public final void finalizeExplosion(boolean particles) {
        clearToBlow();
        super.finalizeExplosion(particles);
    }

}
