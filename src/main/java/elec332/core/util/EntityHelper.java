package elec332.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 13-8-2018.
 */
public class EntityHelper {

    public static Entity createEntity(ResourceLocation name, World world) {
        return EntityList.createEntityByIDFromName(new ResourceLocation(name.toString().toLowerCase()), world);
    }

    public static void smiteEntity(DamageSource source, EntityLivingBase target) {  //non-buggy version by InfinityRaider
        target.setHealth(0);
        target.onDeath(source);
        target.setDead();
    }

}
