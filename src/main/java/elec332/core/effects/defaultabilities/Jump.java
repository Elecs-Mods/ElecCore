package elec332.core.effects.defaultabilities;

import elec332.core.util.RegistryHelper;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class Jump extends AbstractPotionAbility {

    public Jump() {
        super("jump");
    }

    @Override
    public Potion getPotion() {
        return RegistryHelper.getPotionRegistry().getObject(new ResourceLocation("jump_boost"));
    }

}
