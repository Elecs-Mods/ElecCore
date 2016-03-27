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
        if (AbilityHelper.isEffectActive(event.getEntityLiving(), this) && event.getEntityLiving().isInWater()){
            event.getEntityLiving().motionX *= 1.08;
            event.getEntityLiving().motionZ *= 1.08;
        }
    }
}
