package elec332.core.effects.defaultabilities;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

/**
 * Created by Elec332 on 27-9-2015.
 */
public class Invisibility extends AbstractPotionAbility {

    public Invisibility() {
        super("invisibility");
    }

    @Override
    public Potion getPotion() {
        return GameData.getPotionRegistry().getObject(new ResourceLocation("invisibility"));
    }

}
