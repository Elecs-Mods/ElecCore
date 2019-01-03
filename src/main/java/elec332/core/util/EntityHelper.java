package elec332.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 13-8-2018.
 */
public class EntityHelper {

    /**
     * Creates an entity by a {@link ResourceLocation}
     *
     * @param name  The entity name in the registry
     * @param world The world in which the entity has to be created
     * @return The new entity, created from the provided name
     */
    @Nullable
    public static Entity createEntity(ResourceLocation name, World world) {
        EntityType<?> entityType = RegistryHelper.getEntities().getValue(name);
        return entityType == null ? null : entityType.create(world);
    }

    /**
     * Insta-kills an entity without glitches, with the specified {@link DamageSource} as cause of death.
     *
     * @param source The cause of death
     * @param target The entity that will be dead very soon
     */
    public static void smiteEntity(DamageSource source, EntityLivingBase target) {  //non-buggy version by InfinityRaider
        target.setHealth(0);
        target.onDeath(source);
        //target.setDead();
    }

}
