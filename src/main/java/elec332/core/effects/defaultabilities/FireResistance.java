package elec332.core.effects.defaultabilities;

import elec332.core.effects.api.ability.Ability;
import elec332.core.effects.api.util.AbilityHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Elec332 on 27-9-2015.
 */
@SuppressWarnings("unused")
public class FireResistance extends Ability {

    public FireResistance() {
        super("fireresistance");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event){
        if (event.getEntityLiving() instanceof EntityPlayerMP && (event.getSource() == DamageSource.ON_FIRE || event.getSource() == DamageSource.IN_FIRE) && AbilityHelper.isEffectActive(event.getEntityLiving(), this)) {
            event.setCanceled(true);
        }
    }

}
