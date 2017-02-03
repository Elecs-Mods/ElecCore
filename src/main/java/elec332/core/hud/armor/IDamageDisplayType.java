package elec332.core.hud;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 8-1-2017.
 */
public interface IDamageDisplayType {

    public String getDamageForDisplay(@Nonnull ItemStack stack);

}
