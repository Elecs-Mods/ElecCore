package elec332.core.compat.jei;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 5-1-2020
 */
public interface IHasSpecialSubtypes {

    public String getIdentifier(@Nonnull ItemStack stack);

}
