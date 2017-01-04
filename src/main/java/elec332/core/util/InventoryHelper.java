package elec332.core.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 27-3-2015.
 */
public class InventoryHelper {

    public static MinecraftList<ItemStack> newItemStackList(int size){
        return MinecraftList.create(size, ItemStackHelper.NULL_STACK);
    }

    public static void readItemsFromNBT(@Nonnull NBTTagCompound data, @Nonnull List<ItemStack> items){
        items.clear();
        NBTTagList nbttaglist = data.getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < items.size()) {
                items.set(j, ItemStackHelper.loadItemStackFromNBT(nbttagcompound));
            }
        }
    }

    public static NBTTagCompound writeItemsToNBT(@Nonnull List<ItemStack> items){
        return writeItemsToNBT(new NBTTagCompound(), items);
    }

    public static NBTTagCompound writeItemsToNBT(@Nonnull NBTTagCompound tag, @Nonnull List<ItemStack> items){
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < items.size(); ++i) {
            ItemStack itemstack = items.get(i);
            if (ItemStackHelper.isStackValid(itemstack)) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        tag.setTag("Items", nbttaglist);
        return tag;
    }

    /*
     * I don't want to depend on MC methods where possible.
     */
    @Nonnull
    @Deprecated
    public static ItemStack copyItemStack(@Nullable ItemStack stack){
        return ItemStackHelper.copyItemStack(stack);
    }

    public static boolean addItemToInventory(IItemHandler inventory, ItemStack itemstack, boolean simulate) {
        int invSize = getMainInventorySize(inventory);
        return addItemToInventory(inventory, itemstack, 0, invSize, simulate);
    }

    public static int getMainInventorySize(IItemHandler inv) {
        return inv.getSlots();
    }

    public static boolean addItemToInventory(IItemHandler inventory, ItemStack itemstack, int start, int end, boolean simulate) {
        /*List<Integer> possSlots = Lists.newArrayList();
        ItemStack copiedStack = itemstack.copy();
        boolean emptied = false;
        for (int i = start; i < end; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (areEqualNoSize(stack, itemstack)){
                possSlots.add(i);
                copiedStack = inventory.insertItem(i, copiedStack, true);
                emptied = !ItemStackHelper.isStackValid(copiedStack);
                if (emptied){
                    if (simulate){
                        return true;
                    } else {
                        break;
                    }
                }
            }
        }
        if (!emptied) {
            for (int i = start; i < end; i++) {
                if (!possSlots.contains(i)) {
                    copiedStack = inventory.insertItem(i, copiedStack, true);
                    if (!ItemStackHelper.isStackValid(copiedStack)){
                        if (simulate){
                            return true;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if (ItemStackHelper.isStackValid(copiedStack)){
            return false;
        }
        copiedStack = itemstack.copy();
        for (int i : possSlots){
            copiedStack = inventory.insertItem(i, copiedStack, false);
            if (!ItemStackHelper.isStackValid(copiedStack)){
                return true;
            }
        }
        for (int i = start; i < end; i++) {
            if (!possSlots.contains(i)) {
                copiedStack = inventory.insertItem(i, copiedStack, false);
                if (!ItemStackHelper.isStackValid(copiedStack)){
                    return true;
                }
            }
        }
        return false;*/
        return !ItemStackHelper.isStackValid(ItemHandlerHelper.insertItemStacked(inventory, itemstack, simulate));
    }

    public static boolean areNBTsEqual(ItemStack first, ItemStack second) {
        return ItemStack.areItemStackTagsEqual(first, second);
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

    public static int getEmptySlot(IItemHandler inventory, int start, int end) {
        for (int i = start; i < end; i++) {
            if (!ItemStackHelper.isStackValid(inventory.getStackInSlot(i))) {
                return i;
            }
        }
        return -1;
    }

    public static void setContents(IItemHandlerModifiable inventory, List<ItemStack> list) {
        if (inventory.getSlots() != list.size()) {
            System.out.println("Error copying inventory contents!");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            inventory.setStackInSlot(i, ItemStackHelper.copyItemStack(list.get(i)));
        }
    }

    public static List<ItemStack> storeContents(IItemHandler inventory) {
        List<ItemStack> copy = new ArrayList<ItemStack>(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            copy.add(i, ItemStackHelper.copyItemStack(inventory.getStackInSlot(i)));
        }
        return copy;
    }

    public static int amountOfOreDictItemsInventoryHas(IItemHandler inventory, String s, int i){
        int total = 0;
        if (doesInventoryHaveOreDictItem(inventory, s)){
            for (ItemStack oreStack : OreDictionary.getOres(s)){
                for (int p = 0; p < amountOfItemsInventoryHas(inventory, oreStack); p++)
                    total++;
            }
        }
        return total;
    }

    public static boolean doesInventoryHaveOreDictItem(IItemHandler inventory, String s){
        for (ItemStack stack : OreDictionary.getOres(s)){
            if (getFirstSlotWithItemStackNoNBT(inventory, stack) != -1) {
                return true;
            }
        }
        return false;
    }

    public static int amountOfItemsInventoryHas(IItemHandler inventory, ItemStack stack){
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

    public static Integer[] getSlotsWithItemStackNoNBT(IItemHandler inventory, ItemStack stack){
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for(int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stackInSlot = inventory.getStackInSlot(i);
            if(ItemStackHelper.isStackValid(stackInSlot) && stack != null && stackInSlot.getItem() == stack.getItem()) {
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

    public static int getFirstSlotWithItemStackNoNBT(IItemHandler inventory, ItemStack stack){
        for(int i = 0; i < inventory.getSlots(); ++i) {
            ItemStack stackInSlot = inventory.getStackInSlot(i);
            if(ItemStackHelper.isStackValid(stackInSlot) && stack != null && stackInSlot.getItem() == stack.getItem()) {
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