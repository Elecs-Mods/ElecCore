package elec332.core.effects.defaultabilities;

import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.ability.WrappedAbility;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

/**
 * Created by Elec332 on 27-9-2015.
 */
public abstract class AbstractPotionAbility extends Ability {

    public AbstractPotionAbility(String name) {
        super(name);
    }

    public abstract int getPotionID();

    @Override
    public void updateEffectOnEntity(EntityLivingBase entity, WrappedAbility activeEffect) {
        entity.addPotionEffect(new PotionEffect(getPotionID(), 5, activeEffect.getStrength()));
    }

    @Override
    public void onEffectRemovedFromEntity(EntityLivingBase entity, WrappedAbility activeEffect) {
        entity.removePotionEffect(getPotionID());
    }

}
