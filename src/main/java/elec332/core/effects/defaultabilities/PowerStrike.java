package elec332.core.effects.defaultabilities;

import net.minecraft.potion.Potion;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class PowerStrike extends AbstractPotionAbility {

    public PowerStrike() {
        super("powerstrike");
    }

    @Override
    public int getPotionID() {
        return Potion.damageBoost.getId();
    }
}
