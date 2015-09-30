package elec332.core.effects.defaultabilities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.AbilityHelper;
import elec332.core.util.EventHelper;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class Climb extends Ability {

    public Climb() {
        super("climb");
        setMaxLevel(1);
        EventHelper.registerHandlerFML(this);
    }

    @SubscribeEvent
    public void makeClimb(TickEvent.PlayerTickEvent event){
        if (AbilityHelper.isEffectActive(event.player, this) && event.player.isCollidedHorizontally){
            event.player.motionY = 0.1176D;
            event.player.fallDistance = 0.0f;
        }
    }
}
