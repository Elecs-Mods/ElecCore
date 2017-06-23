package elec332.core.util.recipes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.api.registry.ICraftingManager;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreIngredient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Elec332 on 27-11-2016.
 */
public class RecipeHelper {

    private static final ICraftingManager craftingManager;

    public static ICraftingManager getCraftingManager(){
        return craftingManager;
    }

    public static ElecCoreFurnaceManager getFurnaceManager(){
        return ElecCoreFurnaceManager.getInstance();
    }

    private static void registerParsers(){
        craftingManager.registerIngredientParser(o -> o instanceof Item ? Ingredient.func_193367_a((Item) o) : null);
        craftingManager.registerIngredientParser(o -> o instanceof Block ? Ingredient.func_193369_a(new ItemStack((Block) o)) : null);
        craftingManager.registerIngredientParser(o -> o instanceof ItemStack ? Ingredient.func_193369_a((ItemStack) o) : null);
        craftingManager.registerIngredientParser(o -> o instanceof String ? new OreIngredient((String) o) : null);
    }

    static {
        craftingManager = new ICraftingManager() {

            private final Set<Function<Object, Ingredient>> parsers = Sets.newHashSet();

            @Override
            public Iterable<IRecipe> getRecipes() {
                return CraftingManager.field_193380_a;
            }

            @Override
            public void registerRecipe(IRecipe recipe, ResourceLocation name) {
                CraftingManager.func_193372_a(name, recipe);
            }

            @Override
            public void addShapelessRecipe(ResourceLocation name, ItemStack stack, Object... recipeComponents) {
                NonNullList<Ingredient> ingredients = NonNullList.create();
                for (Object o : recipeComponents){
                    ingredients.add(parseIngredient(o));
                }
                registerRecipe(new ShapelessRecipes(name.toString(), stack, ingredients), name);
            }

            @Override
            public void registerIngredientParser(Function<Object, Ingredient> parser) {
                this.parsers.add(parser);
            }

            private Ingredient parseIngredient(Object o){
                for (Function<Object, Ingredient> parser : parsers){
                    Ingredient ingred = parser.apply(o);
                    if (ingred != null){
                        return ingred;
                    }
                }
                throw new IllegalArgumentException();
            }

            @Override
            public void addRecipe(ResourceLocation name, ItemStack stack, Object... recipeComponents) {
                String s = "";
                int i = 0;
                int j = 0;
                int k = 0;

                if (recipeComponents[i] instanceof String[]) {
                    String[] astring = ((String[])recipeComponents[i++]);

                    for (String s2 : astring) {
                        ++k;
                        j = s2.length();
                        s = s + s2;
                    }

                } else {

                    while (recipeComponents[i] instanceof String) {
                        String s1 = (String)recipeComponents[i++];
                        ++k;
                        j = s1.length();
                        s = s + s1;
                    }

                }

                Map<Character, Ingredient> map;

                for (map = Maps.newHashMap(); i < recipeComponents.length; i += 2) {
                    Character character = (Character)recipeComponents[i];
                    Ingredient ing =
                            parseIngredient(recipeComponents[i + 1]);
                            /*Ingredient.field_193370_a;

                    Object component = recipeComponents[i + 1];
                    if (component instanceof Item) {
                        ing = Ingredient.func_193367_a((Item)recipeComponents[i + 1]);
                    } else if (component instanceof Block) {
                        ing = Ingredient.func_193369_a(new ItemStack((Block)recipeComponents[i + 1], 1, 32767));
                    } else if (component instanceof ItemStack) {
                        ing = Ingredient.func_193369_a((ItemStack)recipeComponents[i + 1]);
                    } else if (component instanceof String){
                        ing = Ingredient.func_193369_a(OredictHelper.getOres((String) component).toArray(new ItemStack[0]));
                    }*/


                    map.put(character, ing);
                }

                NonNullList<Ingredient> ingredients = NonNullList.withSize(j * k, Ingredient.field_193370_a);

                for (int l = 0; l < j * k; ++l) {
                    char c0 = s.charAt(l);
                    if (map.containsKey(c0)) {
                        ingredients.set(l, map.get(c0));
                    }
                }

                registerRecipe(new ShapedRecipes(name.toString(), j, k, ingredients, stack), name);

            }

            @Override
            public ItemStack findMatchingRecipe(InventoryCrafting craftMatrix, World world) {
                return CraftingManager.findMatchingRecipe(craftMatrix, world);
            }

            @Override
            public List<ItemStack> getRemainingItems(InventoryCrafting craftMatrix, World world) {
                return CraftingManager.getRemainingItems(craftMatrix, world);
            }

        };
        registerParsers();

    }

}
