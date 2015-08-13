package elec332.core.explosion;

import com.google.common.collect.Lists;
import elec332.core.util.BlockLoc;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Elec332 on 13-8-2015.
 */
public abstract class AbstractExplosion extends Explosion {

    public AbstractExplosion(World world, Entity entity, double x, double y, double z, float size) {
        super(world, entity, x, y, z, size);
        this.world = world;
        this.location = new BlockLoc((int)explosionX, (int)explosionY, (int)explosionZ);
        this.entitiesInRange = Lists.newArrayList();
    }

    private final World world;
    private final BlockLoc location;
    private List<Entity> entitiesInRange;

    public void explode(){
        ExplosionEvent event = new ExplosionEvent.Detonate(world, this, null);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()){
            preExplode();
            doExplode();
            postExplode();
        }
    }

    protected abstract void preExplode();

    protected abstract void doExplode();

    protected abstract void postExplode();

    protected void damageEntities(float radius, float power){
        if (!world.isRemote) {
            radius *= 2.0f;
            BlockLoc minCoord = location.copy();
            minCoord.add(-radius - 1);
            BlockLoc maxCoord = location.copy();
            maxCoord.add(radius + 1);
            @SuppressWarnings("unchecked")
            List<Entity> allEntities = world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(minCoord.xCoord, minCoord.yCoord, minCoord.zCoord, maxCoord.xCoord, maxCoord.yCoord, maxCoord.zCoord));
            Collections.sort(allEntities, new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return (int)(getDistance(o1) - getDistance(o2));
                }
            });
            for (Entity entity : allEntities) {
                double distance = getDistance(entity) / radius;
                if (distance <= 1.0D) {
                    double xDifference = entity.posX - location.xCoord;
                    double yDifference = entity.posY - location.yCoord;
                    double zDifference = entity.posZ - location.zCoord;
                    double d1 = MathHelper.sqrt_double(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                    xDifference /= d1;
                    yDifference /= d1;
                    zDifference /= d1;
                    double density = world.getBlockDensity(Vec3.createVectorHelper(location.xCoord, location.yCoord, location.zCoord), entity.boundingBox);
                    double d2 = (1.0D - distance) * density;
                    int damage = (int) ((d2 * d2 + d2) / 2.0D * 8.0D * power + 1.0D);
                    entity.attackEntityFrom(DamageSource.setExplosionSource(this), damage);
                    entity.motionX += xDifference * d2;
                    entity.motionY += yDifference * d2;
                    entity.motionZ += zDifference * d2;
                }
            }
        }
    }

    private double getDistance(Entity entity){
        return entity.getDistance(location.xCoord, location.yCoord, location.zCoord);
    }

    public final float getRadius(){
        return this.explosionSize;
    }


    public final BlockLoc getLocation(){
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
