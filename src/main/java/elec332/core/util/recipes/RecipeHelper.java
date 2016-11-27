package elec332.core.util.recipes;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;

/**
 * Created by Elec332 on 27-11-2016.
 */
public class RecipeHelper {

    public static CraftingManager getCraftingManager(){
        return CraftingManager.getInstance();
    }

    public static ElecCoreFurnaceManager getFurnaceManager(){
        return ElecCoreFurnaceManager.getInstance();
    }

    public static List<Object> getRecipeOutput(ShapelessOreRecipe recipe){
        return recipe.getInput();
    }

    public static void registerRecipeSorter(ResourceLocation name, Class<? extends IRecipe> recipeClass){
        registerRecipeSorter(name, recipeClass, RecipeSorter.Category.SHAPELESS);
    }

    public static void registerRecipeSorter(ResourceLocation name, Class<? extends IRecipe> recipeClass, RecipeSorter.Category category){
        RecipeSorter.register(name.toString(), recipeClass, category, "");
    }

}
