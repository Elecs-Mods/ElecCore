package elec332.core.helper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 3-1-2015.
 */
public class recipeHelper {
    public static void addStorageRecipe(Item Input, Item Output){
        GameRegistry.addShapedRecipe(new ItemStack(Output), new Object[]{
                "III", "III", "III",'I',new ItemStack(Input)});
    }
}
