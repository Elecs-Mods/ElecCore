package elec332.core.effects.defaultabilities;

import net.minecraft.potion.Potion;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class Invisibility extends AbstractPotionAbility {

    public Invisibility() {
        super("invisibility");
    }

    @Override
    public int getPotionID() {
        return Potion.invisibility.getId();
    }
}
