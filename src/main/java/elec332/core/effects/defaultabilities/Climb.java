package elec332.core.effects.defaultabilities;

import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.AbilityHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by Elec332 on 27-9-2015.
 */
@SuppressWarnings("unused")
public class Climb extends Ability {

    public Climb() {
        super("climb");
        setMaxLevel(1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void makeClimb(TickEvent.PlayerTickEvent event){
        if (AbilityHelper.isEffectActive(event.player, this) && event.player.isCollidedHorizontally){
            event.player.motionY = 0.1176D;
            event.player.fallDistance = 0.0f;
        }
    }
}
