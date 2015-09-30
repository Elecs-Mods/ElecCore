package elec332.core.effects.defaultabilities;

import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.ability.WrappedAbility;
import elec332.core.player.PlayerHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class Flight extends Ability {

    public Flight() {
        super("flight");
    }

    @Override
    public void onEffectAddedToEntity(EntityLivingBase entity, WrappedAbility activeEffect) {
        if (entity instanceof EntityPlayer)
            PlayerHelper.activateFlight((EntityPlayer) entity);
    }

    @Override
    public void onEffectRemovedFromEntity(EntityLivingBase entity, WrappedAbility activeEffect) {
        if (entity instanceof EntityPlayer)
            PlayerHelper.deactivateFlight((EntityPlayer) entity);
    }
}
