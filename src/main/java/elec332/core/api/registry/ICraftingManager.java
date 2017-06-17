package elec332.core.api.registry;

import com.google.common.collect.Lists;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.util.List;

/**
 * Created by Elec332 on 16-6-2017.
 */
public interface ICraftingManager {

    public Iterable<IRecipe> getRecipes();

    public default void registerRecipe(IRecipe recipe){
        registerRecipe(recipe, recipe.toString());
    }

    @SuppressWarnings("all")
    public default void registerRecipe(IRecipe recipe, String name){
        registerRecipe(recipe, new ResourceLocation(Loader.instance().activeModContainer().getModId(), name));
    }

    public void registerRecipe(IRecipe recipe, ResourceLocation name);

    @Deprecated
    public default void addShapelessRecipe(ItemStack stack, Object... recipeComponents){
        addShapelessRecipe(stack.toString() + Lists.newArrayList(recipeComponents), stack, recipeComponents);
    }

    @SuppressWarnings("all")
    public default void addShapelessRecipe(String name, ItemStack stack, Object... recipeComponents){
        addShapelessRecipe(new ResourceLocation(Loader.instance().activeModContainer().getModId(), name), stack, recipeComponents);
    }

    public void addShapelessRecipe(ResourceLocation name, ItemStack stack, Object... recipeComponents);

    @Deprecated
    public default void addRecipe(ItemStack stack, Object... recipeComponents){
        addRecipe(stack.toString() + Lists.newArrayList(recipeComponents), stack, recipeComponents);
    }

    @SuppressWarnings("all")
    public default void addRecipe(String name, ItemStack stack, Object... recipeComponents){
        addRecipe(new ResourceLocation(Loader.instance().activeModContainer().getModId(), name), stack, recipeComponents);
    }

    public void addRecipe(ResourceLocation name, ItemStack stack, Object... recipeComponents);

    public ItemStack findMatchingRecipe(InventoryCrafting craftMatrix, World world);

    public List<ItemStack> getRemainingItems(InventoryCrafting craftMatrix, World world);

}
