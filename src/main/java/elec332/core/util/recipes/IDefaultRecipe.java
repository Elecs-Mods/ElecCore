package elec332.core.util.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 26-11-2016.
 */
public interface IDefaultRecipe extends IRecipe {

    @Override
    @Nonnull
    default public ItemStack[] getRemainingItems(@Nonnull InventoryCrafting inv){
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

}
