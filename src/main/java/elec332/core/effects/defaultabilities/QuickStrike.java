package elec332.core.effects.defaultabilities;

import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.AbilityHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Elec332 on 27-9-2015.
 */
@SuppressWarnings("unused")
public class QuickStrike extends Ability {

    public QuickStrike() {
        super("quickstrike");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onHit(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof EntityLivingBase && AbilityHelper.isEffectActive((EntityLivingBase) event.getSource().getEntity(), this)) {
            if (event.getEntityLiving().hurtResistantTime > 15) {
                event.getEntityLiving().hurtResistantTime -= 13;
            }
        }
    }
}
