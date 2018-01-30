package elec332.core.compat.jei;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 22-1-2018.
 */
public interface IHasSpecialSubtypes {

    public String getIdentifier(@Nonnull ItemStack stack);

}
