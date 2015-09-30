package elec332.core.effects.defaultabilities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.AbilityHelper;
import elec332.core.util.EventHelper;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class WaterBreathing extends Ability {

    public WaterBreathing() {
        super("waterbreathing");
        EventHelper.registerHandlerForge(this);
    }

    @SubscribeEvent
    public void resetAir(LivingEvent.LivingUpdateEvent event){
        if (AbilityHelper.isEffectActive(event.entityLiving, this) && event.entityLiving.getAir() < 300){
            event.entityLiving.setAir(300);
        }
    }
}
