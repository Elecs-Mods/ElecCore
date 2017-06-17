package elec332.core.mcabstractionlayer.impl;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 29-1-2017.
 */
public interface IMCAbstractedRecipe extends IRecipe {

    @Override
    @Nonnull
    default public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv){
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

}
