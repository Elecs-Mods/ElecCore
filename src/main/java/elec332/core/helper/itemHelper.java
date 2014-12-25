package elec332.core.helper;

import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 23-12-2014.
 */
public class itemHelper {

    public static ItemStack getNuggetFromOre(String Ore){
        return getNuggetFromOre(Ore, 1);
    }

    public static ItemStack getNuggetFromOre(String ore, int Amount){
        String nuggetName = ore.replace("ore", "nugget");
        try{
            return new ItemStack(oredictHelper.getFirstOredictEntry(nuggetName),Amount , oredictHelper.getFirstOredictItemDamage(nuggetName));
        }catch (Exception e){

        }
        return null;
    }
}
