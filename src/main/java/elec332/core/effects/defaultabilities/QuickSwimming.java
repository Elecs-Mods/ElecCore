package elec332.core.effects.defaultabilities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.AbilityHelper;
import elec332.core.util.EventHelper;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class QuickSwimming extends Ability {

    public QuickSwimming() {
        super("quickswim");
        EventHelper.registerHandlerForge(this);
    }

    @SubscribeEvent
    public void onTick(LivingEvent.LivingUpdateEvent event){
        if (AbilityHelper.isEffectActive(event.entityLiving, this) && event.entityLiving.isInWater()){
            event.entityLiving.motionX = event.entityLiving.motionX *1.08;
            event.entityLiving.motionZ = event.entityLiving.motionZ *1.08;
        }
    }
}
