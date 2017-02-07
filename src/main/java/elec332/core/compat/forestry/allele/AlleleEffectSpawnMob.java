package elec332.core.compat.forestry.allele;

import elec332.core.compat.forestry.EffectData;
import elec332.core.util.EntityHelper;
import elec332.core.world.WorldHelper;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 26-8-2016.
 */
public class AlleleEffectSpawnMob extends AlleleEffectThrottled {

    public AlleleEffectSpawnMob(ResourceLocation rl, ResourceLocation mobType) {
        super(rl);
        this.requiresWorkingQueen = true;
        this.mobType = mobType;
    }

    public AlleleEffectSpawnMob(String s, ResourceLocation mobType) {
        super(s);
        this.requiresWorkingQueen = true;
        this.mobType = mobType;
    }

    public AlleleEffectSpawnMob(String uid, String unlocalizedName, ResourceLocation mobType) {
        super(uid, unlocalizedName);
        this.requiresWorkingQueen = true;
        this.mobType = mobType;
    }

    private final ResourceLocation mobType;
    private ResourceLocation mobTypePlayerNear;
    private int chance = 100;
    private int maxMobs = 5;
    private boolean angryOnPlayer = false, playerMustBeNear = false;

    public AlleleEffectSpawnMob setSpawnChance(int chance){
        this.chance = chance;
        return this;
    }

    public AlleleEffectSpawnMob setMaxMobsInArea(int maxMobs){
        this.maxMobs = maxMobs;
        return this;
    }

    public AlleleEffectSpawnMob setOnlySpawnsWhenPlayersNear(){
        this.playerMustBeNear = true;
        return this;
    }

    public AlleleEffectSpawnMob setMobTypeWhenPlayerNear(ResourceLocation mobType){
        this.mobTypePlayerNear = mobType;
        return this;
    }

    public AlleleEffectSpawnMob setAngryOnPlayers(){
        this.angryOnPlayer = true;
        return this;
    }

    @Override
    public AlleleEffectSpawnMob setThrottle(int throttle) {
        return (AlleleEffectSpawnMob) super.setThrottle(throttle);
    }

    @Override
    public IEffectData validateStorage(IEffectData storedData) {
        if (storedData == null){
            return new EffectData(1, 0, 0);
        }
        return storedData;
    }

    @Override
    public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
        World world = housing.getWorldObj();
        if (world.rand.nextInt(100) <= chance){
            EntityPlayer player = getClosestPlayer(genome, housing);
            if (player == null && playerMustBeNear){
                return storedData;
            }
            spawn(player, angryOnPlayer, genome, housing);
        }
        return storedData;
    }

    protected boolean spawn(EntityPlayer player, boolean angry, IBeeGenome genome, IBeeHousing housing){
        Entity entity = null;
        if (mobTypePlayerNear != null && player != null){
            entity = EntityHelper.createEntity(mobTypePlayerNear, housing.getWorldObj());
        }
        if (entity == null){
            entity = EntityHelper.createEntity(mobType, housing.getWorldObj());
        }
        if (entity == null){
            return false;
        }
        if (getEntitiesInRange(genome, housing, entity.getClass()).size() > maxMobs){
            return false;
        }
        Random random = housing.getWorldObj().rand;
        AxisAlignedBB aabb = getBounding(genome, housing);
        entity.setLocationAndAngles(aabb.minX + random.nextDouble() * (aabb.maxX - aabb.minX), aabb.minY + random.nextDouble() * (aabb.maxY - aabb.minY), aabb.minZ + random.nextDouble() * (aabb.maxZ - aabb.minZ), random.nextFloat() * 360 , 0);
        if (WorldHelper.spawnEntityInWorld(housing.getWorldObj(), entity)){
            if (entity instanceof EntityLiving && angry && player != null){
                ((EntityLiving) entity).setAttackTarget(player);
            }
            return true;
        }
        return false;
    }

    @Nullable
    protected EntityPlayer getClosestPlayer(IBeeGenome genome, IBeeHousing housing){
        List<EntityPlayer> players = getEntitiesInRange(genome, housing, EntityPlayer.class);
        Collections.sort(players, new Comparator<EntityPlayer>() {
            @Override
            public int compare(EntityPlayer o1, EntityPlayer o2) {
                return (int) (o1.getDistanceSq(housing.getCoordinates()) - o2.getDistanceSq(housing.getCoordinates()));
            }
        });
        for (EntityPlayer player : players){
            if (BeeManager.armorApiaristHelper.wearsItems(player, getUID(), true) >= 4){
                return player;
            }
        }
        return null;
    }

}
