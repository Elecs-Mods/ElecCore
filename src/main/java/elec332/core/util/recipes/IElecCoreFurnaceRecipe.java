package elec332.core.util.recipes;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 13-1-2016.
 */
public interface IElecCoreFurnaceRecipe {

    public boolean accepts(@Nonnull ItemStack input);

    public ItemStack getOutput(@Nonnull ItemStack input);

    public float getExperience(@Nonnull ItemStack input);

}
