package elec332.core.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-5-2016.
 */
public class SafeWrappedIItemHandler implements IItemHandlerModifiable {

    public static SafeWrappedIItemHandler of(IItemHandlerModifiable i){
        return new SafeWrappedIItemHandler(i);
    }

    private SafeWrappedIItemHandler(IItemHandlerModifiable itemHandler){
        this.itemHandler = itemHandler;
    }

    private IItemHandlerModifiable itemHandler;

    public void clear(){
        itemHandler = null;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (itemHandler == null){
            return;
        }
        itemHandler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return itemHandler == null ? 0 : itemHandler.getSlots();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return itemHandler == null ? ItemStackHelper.NULL_STACK : itemHandler.getStackInSlot(slot);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return itemHandler == null ? stack : itemHandler.insertItem(slot, stack, simulate);
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return itemHandler == null ? ItemStackHelper.NULL_STACK : itemHandler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return itemHandler == null ? 0 : itemHandler.getSlotLimit(slot);
    }

}
