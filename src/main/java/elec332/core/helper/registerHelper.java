package elec332.core.helper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 19-12-2014.
 */
public class RegisterHelper {

    public void registerBlock(Block block){
        registerBlock(block, block.getUnlocalizedName().substring(5));
    }

    public static void registerBlock(Block block, String blockname){
        GameRegistry.registerBlock(block, blockname);
    }

    public static void registerItem(Item item){
        registerItem(item, item.getUnlocalizedName().substring(5));
    }

    public static void registerItem(Item item, String itemName){
        GameRegistry.registerItem(item, itemName);
    }

    public static void registerOreDict(Item item, String OreDictname){
        OreDictionary.registerOre(OreDictname, new ItemStack(item));
    }
}
