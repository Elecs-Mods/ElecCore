package elec332.core.helper;

import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 23-12-2014.
 */
public class itemHelper {
    public static ItemStack getNuggetFormOre(String ore){
        String nuggetName = ore.replace("ore", "nugget");
        try{
            return new ItemStack(oredictHelper.getFirstOredictEntry(nuggetName),1 , oredictHelper.getFirstOredictItemDamage(nuggetName));
        }catch (Exception e){

        }
        return null;
    }
}
