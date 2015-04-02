package elec332.core.helper;

import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 23-12-2014.
 */
public class ItemHelper {

    public static boolean areItemsEqual(ItemStack itemStack1, ItemStack itemStack2){
        return itemStack1 != null && itemStack2 != null && itemStack1.getItem() == itemStack2.getItem() && itemStack1.getItemDamage() == itemStack2.getItemDamage();
    }

    public static ItemStack getNuggetFromOre(String Ore){
        return getNuggetFromOre(Ore, 1);
    }

    public static ItemStack getNuggetFromOre(String ore, int Amount){
        String nuggetName = ore.replace("ore", "nugget");
        try{
            return new ItemStack(OredictHelper.getFirstOredictEntry(nuggetName),Amount , OredictHelper.getFirstOredictItemDamage(nuggetName));
        }catch (Exception e){
            return null;
        }
    }
}
