package elec332.core.explosion;

import elec332.core.util.EntityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
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

    public AbstractExplosion(World world, Entity entity, double x, double y, double z, float size) {
        super(world, entity, x, y, z, size, false, Mode.DESTROY);
        this.world = world;
        this.location = new BlockPos((int) x, (int) y, (int) z);
    }

    private final World world;
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
        if (!world.isRemote) {
            radius *= 2.0f;
            BlockPos minCoord = new BlockPos(location);
            float minR = -radius - 1;
            float maxR = radius + 1;
            minCoord.add(minR, minR, minR);
            BlockPos maxCoord = new BlockPos(location);
            maxCoord.add(maxR, maxR, maxR);
            List<LivingEntity> allEntities = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(minCoord, maxCoord));
            allEntities.sort((Comparator<Entity>) (o1, o2) -> (int) (getDistance(o1) - getDistance(o2)));
            for (Entity entity : allEntities) {
                double distance = getDistance(entity) / radius;
                if (distance <= 1.0D) {
                    double xDifference = entity.getPosX() - location.getX();
                    double yDifference = entity.getPosY() - location.getY();
                    double zDifference = entity.getPosZ() - location.getZ();
                    double d1 = MathHelper.sqrt(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                    xDifference /= d1;
                    yDifference /= d1;
                    zDifference /= d1;
                    Vec3d motionMod = new Vec3d(xDifference, yDifference, zDifference);
                    double density = Explosion.getBlockDensity(new Vec3d(location.getX(), location.getY(), location.getZ()), entity);
                    double d2 = (1.0D - distance) * density;
                    int damage = (int) ((d2 * d2 + d2) / 2.0D * 8.0D * power + 1.0D);
                    entity.attackEntityFrom(DamageSource.causeExplosionDamage(this), damage);
                    EntityHelper.addMotion(entity, motionMod.mul(d2, d2, d2));
                }
            }
        }
    }

    private double getDistance(Entity entity) {
        return MathHelper.sqrt(entity.getDistanceSq(location.getX(), location.getY(), location.getZ()));
    }

    public final float getRadius() {
        return this.size;
    }


    public final BlockPos getLocation() {
        return location;
    }

    public final World getWorld() {
        return world;
    }

    /**
     * Make vanilla functions useless
     */
    @Override
    public void doExplosionA() {
    }

    @Override
    public void doExplosionB(boolean p_77279_1_) {
    }

}
