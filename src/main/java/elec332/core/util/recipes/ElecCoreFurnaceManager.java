package elec332.core.util.recipes;

import com.google.common.collect.Lists;
import elec332.core.util.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 13-1-2016.
 */
public class ElecCoreFurnaceManager extends FurnaceRecipes {

    @Nonnull
    public static ElecCoreFurnaceManager getInstance(){
        return (ElecCoreFurnaceManager) FurnaceRecipes.instance();
    }

    public ElecCoreFurnaceManager(){
        super();
        this.recipes = Lists.newArrayList();
    }

    private final List<IElecCoreFurnaceRecipe> recipes;

    @Override
    @SuppressWarnings("all")
    public ItemStack getSmeltingResult(ItemStack stack) {
        /* When the vanilla smelting recipes are registered, my list is still null... :( */
        if (ItemStackHelper.isStackValid(stack) && recipes != null) {
            for (IElecCoreFurnaceRecipe recipe : recipes) {
                if (recipe.accepts(stack)) {
                    return recipe.getOutput(stack);
                }
            }
        }
        return super.getSmeltingResult(stack);
    }

    @Override
    @SuppressWarnings("all")
    public float getSmeltingExperience(ItemStack stack) {
        if (ItemStackHelper.isStackValid(stack)) {
            for (IElecCoreFurnaceRecipe recipe : recipes) {
                if (recipe.accepts(stack)) {
                    return recipe.getExperience(stack);
                }
            }
        }
        return super.getSmeltingExperience(stack);
    }

    public void registerRecipe(IElecCoreFurnaceRecipe recipe){
        recipes.add(recipe);
    }

}
