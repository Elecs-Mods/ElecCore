package elec332.core.effects.defaultabilities;

import net.minecraft.potion.Potion;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class Jump extends AbstractPotionAbility {

    public Jump() {
        super("jump");
    }

    @Override
    public int getPotionID() {
        return Potion.jump.getId();
    }
}
