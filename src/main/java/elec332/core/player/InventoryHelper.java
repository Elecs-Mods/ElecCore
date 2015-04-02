package elec332.core.player;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

/**
 * Created by Elec332 on 27-3-2015.
 */
public class InventoryHelper {

    public static int amountOfOreDictItemsInventoryHas(IInventory inventory, String s, int i){
        int total = 0;
        if (doesInventoryHaveOreDictItem(inventory, s)){
            for (ItemStack oreStack : OreDictionary.getOres(s)){
                for (int p = 0; p < amountOfItemsInventoryHas(inventory, oreStack); p++)
                    total++;
            }
        }
        return total;
    }

    public static boolean doesInventoryHaveOreDictItem(IInventory inventory, String s){
        for (ItemStack stack : OreDictionary.getOres(s)){
            if (getFirstSlotWithItemStack(inventory, stack) != -1) {
                return true;
            }
        }
        return false;
    }

    public static int amountOfItemsInventoryHas(IInventory inventory, ItemStack stack){
        int total = 0;
        if (getFirstSlotWithItemStack(inventory, stack) != -1){
            for (int q : getSlotsWithItemStack(inventory, stack)){
                ItemStack inSlot = inventory.getStackInSlot(q);
                for (int p = 0; p < inSlot.stackSize; p++)
                    total++;
            }
        }
        return total;
    }

    public static Integer[] getSlotsWithItemStack(IInventory inventory, ItemStack stack){
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stackInSlot = inventory.getStackInSlot(i);
            if(stackInSlot != null && stack != null && stackInSlot.getItem() == stack.getItem()) {
                if (stackInSlot.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                    ret.add(i);
                if (!stackInSlot.getItem().getHasSubtypes() && !stack.getItem().getHasSubtypes())
                    ret.add(i);
            }
        }
        if (!ret.isEmpty())
            return ret.toArray(new Integer[ret.size()]);
        else return null;
    }

    public static int getFirstSlotWithItemStack(IInventory inventory, ItemStack stack){
        Integer[] slots = getSlotsWithItemStack(inventory, stack);
        if (slots != null)
            return slots[0];
        else return -1;
    }
}
