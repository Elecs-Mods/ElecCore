package elec332.core.util;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
//TODO: Cleanup & document
public class InventoryHelper {

    /**
     * Gets the tooltip for an {@link ItemStack}
     *
     * @param stack The {@link ItemStack} you want get the tooltip from
     * @param playerIn The player holding the stack
     * @param advanced Whether to display extra data
     * @return The tooltip for the provided {@link ItemStack}
     */
    @SideOnly(Side.CLIENT)
    public static List<String> getTooltip(ItemStack stack, @Nullable EntityPlayer playerIn, boolean advanced){
        return stack.getTooltip(playerIn, advanced ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    /**
     * Gets the tooltip for an {@link Item}
     *
     * @param item The {@link Item} you want get the tooltip from
     * @param stack The {@link ItemStack} of the item
     * @param world The world
     * @param tooltip The tooltip list, data will be added to this list
     * @param advanced Whether to display extra data
     * @return The tooltip for the provided {@link Item}
     */
    @SideOnly(Side.CLIENT)
    public static void addInformation(Item item, ItemStack stack, World world, List<String> tooltip, boolean advanced){
        item.addInformation(stack, world, tooltip, advanced ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    /**
     * Reads an inventory from NBT.
     * The list is first cleared, and then the contents will be read from NBT and added to the list
     *
     * @param data The NBT data
     * @param items Inventory representation
     */
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

    /**
     * Writes an inventory to NBT
     *
     * @param items Inventory representation
     * @return The NBT tag with the inventory data
     */
    public static NBTTagCompound writeItemsToNBT(@Nonnull List<ItemStack> items){
        return writeItemsToNBT(new NBTTagCompound(), items);
    }

    /**
     * Writes an inventory to NBT
     *
     * @param tag The base NBT tag, to which the inventory data will be written
     * @param items Inventory representation
     * @return The original NBT tag with the inventory data added
     */
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

    /**
     * Copies the specified {@link ItemStack}, null proof
     *
     * @param stack The stack
     * @return A copy of the provided {@link ItemStack}
     */
    @Nonnull
    @Deprecated
    public static ItemStack copyItemStack(@Nullable ItemStack stack){
        return ItemStackHelper.copyItemStack(stack);
    }

    /**
     * Adds an {@link ItemStack} to an inventory
     *
     * @param inventory The inventory
     * @param itemstack The stack
     * @param simulate Whether to simulate the insertion
     * @return Whether the entire stack was added to the inventory
     */
    public static boolean addItemToInventory(IItemHandler inventory, ItemStack itemstack, boolean simulate) {
        int invSize = getMainInventorySize(inventory);
        return addItemToInventory(inventory, itemstack, 0, invSize, simulate);
    }

    /**
     * Gets the main inventory size
     *
     * @param inv The inventory to check
     * @return The main inventory size
     */
    public static int getMainInventorySize(IItemHandler inv) {
        return inv.getSlots();
    }

    /**
     * Adds an {@link ItemStack} to an inventory
     *
     * @param inventory The inventory
     * @param itemstack The stack
     * @param start Starting index
     * @param end Stop index (exclusive)
     * @param simulate Whether to simulate the insertion
     * @return Whether the entire stack was added to the inventory
     */
    public static boolean addItemToInventory(IItemHandler inventory, ItemStack itemstack, int start, int end, boolean simulate) {
        return !ItemStackHelper.isStackValid(ItemHandlerHelper.insertItemStacked(inventory, itemstack, simulate));
    }

    /**
     * Checks if the NBT data on both stacks is the same
     *
     * @param first ItemStack 1
     * @param second ItemStack 2
     * @return Whether the NBT data on both stacks is the same
     */
    public static boolean areNBTsEqual(ItemStack first, ItemStack second) {
        return ItemStack.areItemStackTagsEqual(first, second);
    }

    /**
     * Checks if the stacks are the same, ignoring NBT data and stack size
     *
     * @param first ItemStack 1
     * @param second ItemStack 2
     * @return Whether the stacks are the same, ignoring NBT data and stack size
     */
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

    /**
     * Checks if the stacks are the same, ignoring stack size
     *
     * @param first ItemStack 1
     * @param second ItemStack 2
     * @return Whether the stacks are the same, ignoring stack size
     */
    public static boolean areEqualNoSize(ItemStack first, ItemStack second) {
        return areEqualNoSizeNoNBT(first, second) && areNBTsEqual(first, second);
    }

    /**
     * Gets the first empty slot in the inventory between the provided bounds
     *
     * @param inventory The inventory to be checked
     * @param start Starting index
     * @param end Stop index (exclusive)
     * @return The first empty slot in the inventory between the provided bounds
     */
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
                for (int p = 0; p < amountOfItemsInventoryHas(inventory, oreStack); p++) {
                    total++;
                }
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
                for (int p = 0; p < inSlot.stackSize; p++) {
                    total++;
                }
            }
        }
        return total;
    }

    public static Integer[] getSlotsWithItemStackNoNBT(IItemHandler inventory, ItemStack stack){
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for(int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stackInSlot = inventory.getStackInSlot(i);
            if(ItemStackHelper.isStackValid(stackInSlot) && stack != null && stackInSlot.getItem() == stack.getItem()) {
                if (stackInSlot.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    ret.add(i);
                }
                if (!stackInSlot.getItem().getHasSubtypes() && !stack.getItem().getHasSubtypes()) {
                    ret.add(i);
                }
            }
        }
        if (!ret.isEmpty()) {
            return ret.toArray(new Integer[ret.size()]);
        }
        return null;
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
