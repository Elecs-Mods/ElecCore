package elec332.core.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-2-2017.
 */
public abstract class WrappedItemHandler implements IItemHandlerModifiable {

    public static IItemHandlerModifiable wrap(@Nonnull final IItemHandler itemHandler, final boolean in, final boolean out){
        return new WrappedItemHandler() {

            @Nonnull
            @Override
            protected IItemHandler getItemHandler() {
                return itemHandler;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return in ? super.insertItem(slot, stack, simulate) : stack;
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return out ? super.extractItem(slot, amount, simulate) : ItemStackHelper.NULL_STACK;
            }

        };
    }

    public static IItemHandlerModifiable wrap(final IItemHandler itemHandler){
        return new WrappedItemHandler() {

            @Nonnull
            @Override
            protected IItemHandler getItemHandler() {
                return itemHandler;
            }

        };
    }

    @Nonnull
    protected abstract IItemHandler getItemHandler();

    @Override
    public int getSlots() {
        return getItemHandler().getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return getItemHandler().getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return getItemHandler().insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return getItemHandler().extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return getItemHandler().getSlotLimit(slot);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (getItemHandler() instanceof IItemHandlerModifiable){
            ((IItemHandlerModifiable) getItemHandler()).setStackInSlot(slot, stack);
            return;
        }
        throw new UnsupportedOperationException();
    }

}
