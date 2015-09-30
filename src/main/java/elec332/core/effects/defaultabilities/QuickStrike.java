package elec332.core.effects.defaultabilities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.AbilityHelper;
import elec332.core.util.EventHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class QuickStrike extends Ability {

    public QuickStrike() {
        super("quickstrike");
        EventHelper.registerHandlerForge(this);
    }

    @SubscribeEvent
    public void onHit(LivingHurtEvent event) {
        if (event.source.getEntity() instanceof EntityLivingBase && AbilityHelper.isEffectActive((EntityLivingBase) event.source.getEntity(), this)) {
            if (event.entityLiving.hurtResistantTime > 15) {
                event.entityLiving.hurtResistantTime -= 13;
            }
        }
    }
}
