package elec332.core.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 2-1-2017.
 */
public class DoubleItemHandler<I1 extends IItemHandlerModifiable, I2 extends IItemHandlerModifiable> implements IElecItemHandler {

    public DoubleItemHandler(I1 i1, I2 i2){
        this.i1 = i1;
        this.i2 = i2;
        this.i1Size = i1.getSlots();
        this.totalSize = this.i1Size + i2.getSlots();
    }

    private final I1 i1;
    private final I2 i2;
    private final int i1Size, totalSize;

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (slot < i1Size){
            i1.setStackInSlot(slot, stack);
        } else {
            i2.setStackInSlot(slot - i1Size, stack);
        }
    }

    @Override
    public int getSlots() {
        return totalSize;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < i1Size){
            return i1.getStackInSlot(slot);
        } else {
            return i2.getStackInSlot(slot - i1Size);
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot < i1Size){
            return i1.insertItem(slot, stack, simulate);
        } else {
            return i2.insertItem(slot - i1Size, stack, simulate);
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < i1Size){
            return i1.extractItem(slot, amount, simulate);
        } else {
            return i2.extractItem(slot - i1Size, amount, simulate);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot < i1Size){
            return InventoryHelper.getSlotStackLimit(i1, slot);
        } else {
            return InventoryHelper.getSlotStackLimit(i2, slot - i1Size);
        }
    }

    public boolean insertItem(ItemStack stack, boolean simulate){
        return InventoryHelper.addItemToInventory(this, stack, simulate);
    }

}
