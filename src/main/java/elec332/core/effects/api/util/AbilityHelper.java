package elec332.core.effects.api.util;

import elec332.core.effects.api.ElecCoreAbilitiesAPI;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.ability.WrappedAbility;
import net.minecraft.entity.EntityLivingBase;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class AbilityHelper {

    private AbilityHelper(){
        throw new IllegalAccessError();
    }

    public static void addEffectToEntity(EntityLivingBase entity, WrappedAbility effect){
        IEntityAbilityProperties handler = getHandler(entity);
        if (handler != null)
            handler.addEffect(effect);
    }

    public static void removeEffectFromEntity(EntityLivingBase entity, WrappedAbility effect){
        IEntityAbilityProperties handler = getHandler(entity);
        if (handler != null)
            handler.removeEffect(effect);
    }

    public static WrappedAbility getActiveEffect(EntityLivingBase entity, Ability ability){
        return getActiveEffect(entity, ability.getName());
    }

    public static WrappedAbility getActiveEffect(EntityLivingBase entity, String effect){
        IEntityAbilityProperties handler = getHandler(entity);
        if (handler != null)
            return handler.getEffect(effect);
        return null;
    }

    public static boolean isEffectActive(EntityLivingBase entity, String effect){
        return getActiveEffect(entity, effect) != null;
    }

    public static boolean isEffectActive(EntityLivingBase entity, Ability ability){
        return getActiveEffect(entity, ability) != null;
    }

    public static IEntityAbilityProperties getHandler(EntityLivingBase entity){
        return (IEntityAbilityProperties) entity.getExtendedProperties(ElecCoreAbilitiesAPI.PROPERTIES_NAME);
    }

}
