package elec332.core.helper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/**
 * Created by Elec332 on 3-1-2015.
 */
public class RecipeHelper {

    public static IRecipe addStorageRecipe(ItemStack Input, Item Output){
        return addStorageRecipe(Input, new ItemStack(Output));
    }

    public static IRecipe addStorageRecipe(Item Input, ItemStack Output){
        return addStorageRecipe(new ItemStack(Input), Output);
    }

    public static IRecipe addStorageRecipe(Item Input, Item Output){
        return addStorageRecipe(new ItemStack(Input), new ItemStack(Output));
    }

    public static IRecipe addStorageRecipe(ItemStack Input, ItemStack Output){
        return GameRegistry.addShapedRecipe(Output, new Object[]{
                "III", "III", "III",'I',Input});
    }
}
