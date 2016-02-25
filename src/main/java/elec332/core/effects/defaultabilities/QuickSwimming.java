package elec332.core.effects.defaultabilities;

import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.AbilityHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Elec332 on 27-9-2015.
 */
@SuppressWarnings("unused")
public class QuickSwimming extends Ability {

    public QuickSwimming() {
        super("quickswim");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(LivingEvent.LivingUpdateEvent event){
        if (AbilityHelper.isEffectActive(event.entityLiving, this) && event.entityLiving.isInWater()){
            event.entityLiving.motionX = event.entityLiving.motionX *1.08;
            event.entityLiving.motionZ = event.entityLiving.motionZ *1.08;
        }
    }
}
