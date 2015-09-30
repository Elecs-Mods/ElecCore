package elec332.core.effects.defaultabilities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.AbilityHelper;
import elec332.core.util.EventHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class FireResistance extends Ability {

    public FireResistance() {
        super("fireresistance");
        EventHelper.registerHandlerForge(this);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event){
        if (event.entityLiving instanceof EntityPlayerMP && (event.source == DamageSource.onFire || event.source == DamageSource.inFire) && AbilityHelper.isEffectActive(event.entityLiving, this))
            event.setCanceled(true);
    }
}
