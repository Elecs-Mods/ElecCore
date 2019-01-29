package elec332.core.util;

import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 27-3-2015.
 */
public class InventoryHelper {

    /**
     * Gets the tooltip for an {@link ItemStack}
     *
     * @param stack    The {@link ItemStack} you want get the tooltip from
     * @param playerIn The player holding the stack
     * @param advanced Whether to display extra data
     * @return The tooltip for the provided {@link ItemStack}
     */
    @OnlyIn(Dist.CLIENT)
    public static List<String> getTooltip(ItemStack stack, @Nullable EntityPlayer playerIn, boolean advanced) {
        return stack.getTooltip(playerIn, advanced ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).stream()
                .map(ITextComponent::getFormattedText)
                .collect(Collectors.toList());
    }

    /**
     * Gets the tooltip for an {@link Item}
     *
     * @param item     The {@link Item} you want get the tooltip from
     * @param stack    The {@link ItemStack} of the item
     * @param world    The world
     * @param tooltip  The tooltip list, data will be added to this list
     * @param advanced Whether to display extra data
     */
    @OnlyIn(Dist.CLIENT)
    public static void addInformation(Item item, ItemStack stack, World world, List<ITextComponent> tooltip, boolean advanced) {
        item.addInformation(stack, world, tooltip, advanced ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    /**
     * Reads an inventory from NBT.
     * The list is first cleared, and then the contents will be read from NBT and added to the list
     *
     * @param data  The NBT data
     * @param items Inventory representation
     */
    @SuppressWarnings("all")
    public static void readItemsFromNBT(@Nonnull NBTTagCompound data, @Nonnull List<ItemStack> items) {
        items.clear();
        NBTTagList nbttaglist = data.getList("Items", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
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
    public static NBTTagCompound writeItemsToNBT(@Nonnull List<ItemStack> items) {
        return writeItemsToNBT(new NBTTagCompound(), items);
    }

    /**
     * Writes an inventory to NBT
     *
     * @param tag   The base NBT tag, to which the inventory data will be written
     * @param items Inventory representation
     * @return The original NBT tag with the inventory data added
     */
    public static NBTTagCompound writeItemsToNBT(@Nonnull NBTTagCompound tag, @Nonnull List<ItemStack> items) {
        NonNullList<ItemStack> wrap = NonNullList.withSize(items.size(), ItemStackHelper.NULL_STACK);
        for (int i = 0; i < items.size(); i++) {
            wrap.set(i, items.get(i));
        }
        return writeItemsToNBT(tag, wrap);
    }

    /**
     * Writes an inventory to NBT
     *
     * @param tag   The base NBT tag, to which the inventory data will be written
     * @param items Inventory representation
     * @return The original NBT tag with the inventory data added
     */
    public static NBTTagCompound writeItemsToNBT(@Nonnull NBTTagCompound tag, @Nonnull NonNullList<ItemStack> items) {
        net.minecraft.inventory.ItemStackHelper.saveAllItems(tag, items);
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
    public static ItemStack copyItemStack(@Nullable ItemStack stack) {
        return ItemStackHelper.copyItemStack(stack);
    }

    /**
     * Adds an {@link ItemStack} to an inventory
     *
     * @param inventory The inventory
     * @param itemstack The stack
     * @param simulate  Whether to simulate the insertion
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
     * @param start     Starting index
     * @param end       Stop index (exclusive)
     * @param simulate  Whether to simulate the insertion
     * @return Whether the entire stack was added to the inventory
     */
    public static boolean addItemToInventory(IItemHandler inventory, ItemStack itemstack, int start, int end, boolean simulate) {
        return !ItemStackHelper.isStackValid(ItemHandlerHelper.insertItemStacked(inventory, itemstack, simulate));
    }

    /**
     * Checks if the NBT data on both stacks is the same
     *
     * @param first  ItemStack 1
     * @param second ItemStack 2
     * @return Whether the NBT data on both stacks is the same
     */
    public static boolean areNBTsEqual(ItemStack first, ItemStack second) {
        return ItemStack.areItemStackTagsEqual(first, second);
    }

    /**
     * Checks if the stacks are the same, ignoring NBT data and stack size
     *
     * @param first  ItemStack 1
     * @param second ItemStack 2
     * @return Whether the stacks are the same, ignoring NBT data and stack size
     */
    private static boolean areEqualNoSizeNoNBT(ItemStack first, ItemStack second) {
        if (first == null || second == null) {
            return first == second;
        }
        return first.getItem() == second.getItem() && first.getDamage() == second.getDamage();
    }

    /**
     * Checks if the stacks are the same, ignoring stack size
     *
     * @param first  ItemStack 1
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
     * @param start     Starting index
     * @param end       Stop index (exclusive)
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

    /**
     * Stores the content of the provided inventory in a {@link List},
     * useful for making a copy of an inventory's contents when performing test operations on it
     *
     * @param inventory The inventory to be copied;
     * @return A copy of the contents of the provided inventory
     */
    public static List<ItemStack> storeContents(IItemHandler inventory) {
        List<ItemStack> copy = Lists.newArrayListWithCapacity(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            copy.add(i, ItemStackHelper.copyItemStack(inventory.getStackInSlot(i)));
        }
        return copy;
    }

    /**
     * Sets the contents of the provided {@link List} to the provided inventory,
     * useful when you want to restore an inventory to a former state
     *
     * @param inventory The inventory
     * @param list The "new" inventory contents
     */
    public static void setContents(IItemHandlerModifiable inventory, List<ItemStack> list) {
        if (inventory.getSlots() != list.size()) {
            System.out.println("Error copying inventory contents!");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            inventory.setStackInSlot(i, ItemStackHelper.copyItemStack(list.get(i)));
        }
    }

}
