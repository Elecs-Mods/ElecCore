package elec332.core.util;

import elec332.core.api.annotations.AbstractionMarker;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 17-11-2016.
 */
public class ItemStackHelper {

    @AbstractionMarker("getInventoryAbstraction")
    public static boolean isStackValid(ItemStack stack){
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @AbstractionMarker("getInventoryAbstraction")
    public static ItemStack copyItemStack(@Nullable ItemStack stack){
        throw new UnsupportedOperationException();
    }

    @AbstractionMarker("getInventoryAbstraction")
    public static ItemStack loadItemStackFromNBT(NBTTagCompound tag){
        throw new UnsupportedOperationException();
    }

    @AbstractionMarker("getInventoryAbstraction")
    public static ItemStack getAndSplit(List<ItemStack> stacks, int index, int amount) {
        throw new UnsupportedOperationException();
    }

    @AbstractionMarker("getInventoryAbstraction")
    public static ItemStack getAndRemove(List<ItemStack> stacks, int index) {
        throw new UnsupportedOperationException();
    }

    @AbstractionMarker("getInventoryAbstraction")
    private static ItemStack getNullStack(){
        throw new UnsupportedOperationException();
    }

    public static final ItemStack NULL_STACK;
    private static final Item NULL_ITEM;

    static {
        NULL_STACK = getNullStack();
        NULL_ITEM = NULL_STACK == null ? null : NULL_STACK.getItem();
    }

}
