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
public class WaterBreathing extends Ability {

    public WaterBreathing() {
        super("waterbreathing");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void resetAir(LivingEvent.LivingUpdateEvent event){
        if (AbilityHelper.isEffectActive(event.entityLiving, this) && event.entityLiving.getAir() < 300){
            event.entityLiving.setAir(300);
        }
    }
}
