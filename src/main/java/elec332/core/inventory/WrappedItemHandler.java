package elec332.core.inventory;

import elec332.core.util.ItemStackHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 15-2-2017.
 * <p>
 * Wraps an {@link IItemHandler}
 */
public abstract class WrappedItemHandler implements IItemHandlerModifiable {

    public static IItemHandlerModifiable wrap(@Nonnull final IItemHandler itemHandler, final boolean in, final boolean out) {
        return wrap(() -> itemHandler, in, out);
    }

    public static IItemHandlerModifiable wrap(@Nonnull final Supplier<IItemHandler> itemHandler, final boolean in, final boolean out) {
        return new WrappedItemHandler() {

            @Nonnull
            @Override
            protected IItemHandler getItemHandler() {
                return itemHandler.get();
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

    public static IItemHandlerModifiable wrap(final IItemHandler itemHandler) {
        return wrap(() -> itemHandler);
    }

    public static IItemHandlerModifiable wrap(final Supplier<IItemHandler> itemHandler) {
        return new WrappedItemHandler() {

            @Nonnull
            @Override
            protected IItemHandler getItemHandler() {
                return itemHandler.get();
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
        if (getItemHandler() instanceof IItemHandlerModifiable) {
            ((IItemHandlerModifiable) getItemHandler()).setStackInSlot(slot, stack);
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack itemStack) {
        return getItemHandler().isItemValid(slot, itemStack);
    }

}
