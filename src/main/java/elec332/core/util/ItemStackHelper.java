package elec332.core.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 17-11-2016.
 */
public class ItemStackHelper {

    public static boolean isStackValid(ItemStack stack){
        return stack != null && stack != NULL_STACK && !stack.isEmpty && !stack.isEmpty();
    }

    @Nonnull
    public static ItemStack copyItemStack(ItemStack stack){
        return stack == null || stack == NULL_STACK ? NULL_STACK : stack.copy();
    }

    public static ItemStack loadItemStackFromNBT(NBTTagCompound tag){
        return new ItemStack(tag);
    }

    public static ItemStack getAndSplit(List<ItemStack> stacks, int index, int amount) {
        return net.minecraft.inventory.ItemStackHelper.getAndSplit(stacks, index, amount);
    }

    public static ItemStack getAndRemove(List<ItemStack> stacks, int index) {
        return net.minecraft.inventory.ItemStackHelper.getAndRemove(stacks, index);
    }

    private static ItemStack getNullStack(){
        return ItemStack.EMPTY;
    }

    public static final ItemStack NULL_STACK;
    private static final Item NULL_ITEM;

    static {
        NULL_STACK = getNullStack();
        NULL_ITEM = NULL_STACK == null ? null : NULL_STACK.getItem();
    }

}
