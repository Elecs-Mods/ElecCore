package elec332.core.player;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 27-3-2015.
 */
public class InventoryHelper {

    public static boolean addItemToInventory(IInventory inventory, ItemStack itemstack) {
        int invSize = getMainInventorySize(inventory);
        return addItemToInventory(inventory, itemstack, 0, invSize);
    }

    public static int getMainInventorySize(IInventory inv) {
        if (inv instanceof InventoryPlayer) {
            return 36;
        }
        return inv.getSizeInventory();
    }

    public static boolean addItemToInventory(IInventory inventory, ItemStack itemstack, int start, int end) {
        List<ItemStack> contents = storeContents(inventory);
        int maxStack = Math.min(inventory.getInventoryStackLimit(), itemstack.getMaxStackSize());
        for (int i = start; i < end; i++) {
            if (areEqualNoSize(itemstack, inventory.getStackInSlot(i))) {
                ItemStack is = inventory.getStackInSlot(i);
                if (is.stackSize >= maxStack) {
                    continue;
                }
                if (is.stackSize + itemstack.stackSize <= maxStack) {
                    is.stackSize += itemstack.stackSize;
                    inventory.markDirty();
                    return true;
                } else {
                    itemstack.stackSize -= maxStack - is.stackSize;
                    is.stackSize = maxStack;
                }
            }
        }
        while (true) {
            int slot = getEmptySlot(inventory, start, end);
            if (slot != -1 && inventory.isItemValidForSlot(slot, itemstack)) {
                if (itemstack.stackSize <= maxStack) {
                    inventory.setInventorySlotContents(slot, itemstack.copy());
                    inventory.markDirty();
                    return true;
                } else {
                    ItemStack is = itemstack.copy();
                    itemstack.stackSize -= maxStack;
                    is.stackSize = maxStack;
                    inventory.setInventorySlotContents(slot, is);
                }
            } else {
                break;
            }
        }
        setContents(inventory, contents);
        return false;
    }

    public static boolean areNBTsEqual(ItemStack first, ItemStack second) {
        if (first == null || second == null) {
            return first == second;
        }
        if (first.stackTagCompound == null || first.stackTagCompound.hasNoTags()) {
            return second.stackTagCompound == null || second.stackTagCompound.hasNoTags();
        }
        if (second.stackTagCompound == null || second.stackTagCompound.hasNoTags()) {
            return false;
        }
        return first.stackTagCompound.equals(second.stackTagCompound);
    }

    public static boolean areEqualNoSizeNoNBT(ItemStack first, ItemStack second) {
        if (first == null || second == null) {
            return first == second;
        }
        if (first.getItem() != second.getItem()) {
            return false;
        }
        if (first.getHasSubtypes() && first.getItemDamage() != second.getItemDamage()) {
            return false;
        }
        return true;
    }

    public static boolean areEqualNoSize(ItemStack first, ItemStack second) {
        return areEqualNoSizeNoNBT(first, second) && areNBTsEqual(first, second);
    }

    public static int getEmptySlot(IInventory inventory, int start, int end) {
        for (int i = start; i < end; i++) {
            if (inventory.getStackInSlot(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public static void setContents(IInventory inventory, List<ItemStack> list) {
        if (inventory.getSizeInventory() != list.size()) {
            System.out.println("Error copying inventory contents!");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            inventory.setInventorySlotContents(i, ItemStack.copyItemStack(list.get(i)));
        }
    }

    public static List<ItemStack> storeContents(IInventory inventory) {
        List<ItemStack> copy = new ArrayList<ItemStack>(inventory.getSizeInventory());
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            copy.add(i, ItemStack.copyItemStack(inventory.getStackInSlot(i)));
        }
        return copy;
    }

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
            if (getFirstSlotWithItemStackNoNBT(inventory, stack) != -1) {
                return true;
            }
        }
        return false;
    }

    public static int amountOfItemsInventoryHas(IInventory inventory, ItemStack stack){
        int total = 0;
        if (getFirstSlotWithItemStackNoNBT(inventory, stack) != -1){
            for (int q : getSlotsWithItemStackNoNBT(inventory, stack)){
                ItemStack inSlot = inventory.getStackInSlot(q);
                for (int p = 0; p < inSlot.stackSize; p++)
                    total++;
            }
        }
        return total;
    }

    public static Integer[] getSlotsWithItemStackNoNBT(IInventory inventory, ItemStack stack){
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

    public static int getFirstSlotWithItemStackNoNBT(IInventory inventory, ItemStack stack){
        for(int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stackInSlot = inventory.getStackInSlot(i);
            if(stackInSlot != null && stack != null && stackInSlot.getItem() == stack.getItem()) {
                if(stackInSlot.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    return i;
                }
                if(!stackInSlot.getItem().getHasSubtypes() && !stack.getItem().getHasSubtypes()) {
                    return i;
                }
            }
        }
        return -1;
    }
}
