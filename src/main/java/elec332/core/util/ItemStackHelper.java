package elec332.core.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 17-11-2016.
 */
public class ItemStackHelper {

    /**
     * Used to check if stacks are "valid" and non-empty
     *
     * @param stack The {@link ItemStack} to be checked
     * @return Whether the provided stack is "valid" and non-empty
     */
    public static boolean isStackValid(ItemStack stack) {
        return stack != null && stack != NULL_STACK && /*!stack.isEmpty &&*/ !stack.isEmpty();
    }

    /**
     * Copies the specified {@link ItemStack}, null proof
     *
     * @param stack The stack
     * @return A copy of the provided {@link ItemStack}
     */
    @Nonnull
    public static ItemStack copyItemStack(@Nullable ItemStack stack) {
        return stack == null || stack == NULL_STACK ? NULL_STACK : stack.copy();
    }

    /**
     * loads an {@link ItemStack} from the provided {@link CompoundNBT}
     *
     * @param tag The NBT data
     * @return The {@link ItemStack} loaded from the provided {@link CompoundNBT}
     */
    public static ItemStack loadItemStackFromNBT(CompoundNBT tag) {
        return ItemStack.read(tag);
    }

    public static ItemStack getAndSplit(List<ItemStack> stacks, int index, int amount) {
        return net.minecraft.inventory.ItemStackHelper.getAndSplit(stacks, index, amount);
    }

    public static ItemStack getAndRemove(List<ItemStack> stacks, int index) {
        return net.minecraft.inventory.ItemStackHelper.getAndRemove(stacks, index);
    }

    private static ItemStack getNullStack() {
        return ItemStack.EMPTY;
    }

    public static final ItemStack NULL_STACK;
    private static final Item NULL_ITEM;

    static {
        NULL_STACK = getNullStack();
        NULL_ITEM = NULL_STACK == null ? null : NULL_STACK.getItem();
    }

}
