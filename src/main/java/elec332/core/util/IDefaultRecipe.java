package elec332.core.util;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 26-11-2016.
 */
public interface IDefaultRecipe extends IRecipe {

    @Override
    @Nonnull
    default public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv){
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

}
