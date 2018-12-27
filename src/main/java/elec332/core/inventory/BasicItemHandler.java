package elec332.core.inventory;

import elec332.core.util.InventoryHelper;
import elec332.core.util.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 3-12-2016.
 * <p>
 * Simple (serializable) implementation of {@link IItemHandlerModifiable}
 */
public class BasicItemHandler implements IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {

    public BasicItemHandler() {
        this(1);
    }

    public BasicItemHandler(int size) {
        stacks = NonNullList.withSize(size, ItemStackHelper.NULL_STACK);
    }

    public BasicItemHandler(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    protected NonNullList<ItemStack> stacks;

    public void setSize(int size) {
        stacks = NonNullList.withSize(size, ItemStackHelper.NULL_STACK);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        validateSlotIndex(slot);
        ItemStack stackInSlot = this.stacks.get(slot);
        if (ItemStack.areItemStacksEqual(stackInSlot, stack) || !isStackValidForSlot(slot, stack)) {
            return;
        }
        this.stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!ItemStackHelper.isStackValid(stack)) {
            return ItemStackHelper.NULL_STACK;
        }
        if (!canInsert(slot, stack) || !isStackValidForSlot(slot, stack)) {
            return stack;
        }

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (ItemStackHelper.isStackValid(existing)) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }

            limit -= existing.getCount();
        }

        if (limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (!ItemStackHelper.isStackValid(existing)) {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStackHelper.NULL_STACK;
    }

    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0 || !canExtract(slot)) {
            return ItemStackHelper.NULL_STACK;
        }

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (!ItemStackHelper.isStackValid(existing)) {
            return ItemStackHelper.NULL_STACK;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, ItemStackHelper.NULL_STACK);
                onContentsChanged(slot);
            }
            return existing;
        } else {
            if (!simulate) {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }


    @Override
    public NBTTagCompound serializeNBT() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        setSize(nbt.hasKey("Size", net.minecraftforge.common.util.Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size());
        InventoryHelper.readItemsFromNBT(nbt, stacks);
        onLoad();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = InventoryHelper.writeItemsToNBT(compound, stacks);
        tag.setInteger("Size", stacks.size());
        return tag;
    }


    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
        }
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    protected void onLoad() {

    }

    protected void onContentsChanged(int slot) {

    }

    public boolean isStackValidForSlot(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    public boolean canExtract(int slot) {
        return true;
    }

    public boolean canInsert(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    public void clear() {
        stacks.clear();
    }

}