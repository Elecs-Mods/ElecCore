package elec332.core.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Elec332 on 3-1-2015.
 */
@Deprecated
public class RecipeHelper {

    public static IRecipe getCraftingRecipe(ItemStack itemStack) {
        for(int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); i++) {
            IRecipe recipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(i);
            if (recipe != null) {
                ItemStack output = recipe.getRecipeOutput();
                if (ItemHelper.areItemsEqual(itemStack, output)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    public static IRecipe addStorageRecipe(ItemStack Input, ItemStack Output){
        return GameRegistry.addShapedRecipe(Output,
                "III", "III", "III",'I',Input);
    }
}
