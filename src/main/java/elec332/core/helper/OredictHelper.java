package elec332.core.helper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Elec332 on 23-12-2014.
 */
public class OredictHelper {
    public static Item getFirstOredictEntry(String Oredictname){
        return getOredictEntry(0, Oredictname);
    }
    public static Item getOredictEntry(int number, String Oredictname){
        return OreDictionary.getOres(Oredictname).get(number).getItem();
    }
    public static int getFirstOredictItemDamage(String Oredictname){
        return getOredictItemDamage(0, Oredictname);
    }
    public static int getOredictItemDamage(int number, String OreDictname){
        return OreDictionary.getOres(OreDictname).get(number).getItemDamage();
    }
    public static ItemStack getFirstOreDictItemWithMeta(String Oredictname){ return getOreDictItemWithMeta(Oredictname, 0); }
    public static ItemStack getOreDictItemWithMeta(String Oredictname, int entry){
        return new ItemStack(getOredictEntry(entry, Oredictname), 1, getOredictItemDamage(entry, Oredictname));
    }
}
