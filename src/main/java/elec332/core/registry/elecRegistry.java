package elec332.core.registry;

import elec332.core.handler.furnaceRecipehandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 21-12-2014.
 */
public class elecRegistry {

    public static void addSecondarySmelting(Block input, ItemStack output)
    {
        furnaceRecipehandler.smelting().func_151393_a(input, output, 0.0F);
    }

    public static void addSecondarySmelting(Item input, ItemStack output)
    {
        furnaceRecipehandler.smelting().func_151396_a(input, output, 0.0F);
    }

    public static void addSecondarySmelting(ItemStack input, ItemStack output)
    {
        furnaceRecipehandler.smelting().func_151394_a(input, output, 0.0F);
    }
}
