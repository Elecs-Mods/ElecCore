package elec332.core.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 17-9-2016.
 */
public interface IDefaultInventory extends IInventory {

    @Nonnull
    public IInventory getInventory();

    @Override
    default public int getSizeInventory() {
        return getInventory().getSizeInventory();
    }

    @Override
    @Nonnull
    default public ItemStack getStackInSlot(int index) {
        return getInventory().getStackInSlot(index);
    }

    @Override
    @Nonnull
    default public ItemStack decrStackSize(int index, int count) {
        return getInventory().decrStackSize(index, count);
    }

    @Override
    @Nonnull
    default public ItemStack removeStackFromSlot(int index) {
        return getInventory().removeStackFromSlot(index);
    }

    @Override
    default public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        getInventory().setInventorySlotContents(index, stack);
    }

    @Override
    default public int getInventoryStackLimit() {
        return getInventory().getInventoryStackLimit();
    }

    @Override
    default public void markDirty() {
        getInventory().markDirty();
    }

    @Override
    default public boolean isUseableByPlayer(@Nonnull EntityPlayer player) {
        return getInventory().isUseableByPlayer(player);
    }

    @Override
    default public void openInventory(@Nonnull EntityPlayer player) {
        getInventory().openInventory(player);
    }

    @Override
    default public void closeInventory(@Nonnull EntityPlayer player) {
        getInventory().closeInventory(player);
    }

    @Override
    default public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return getInventory().isItemValidForSlot(index, stack);
    }

    @Override
    default public int getField(int id) {
        return getInventory().getField(id);
    }

    @Override
    default public void setField(int id, int value) {
        getInventory().setField(id, value);
    }

    @Override
    default public int getFieldCount() {
        return getInventory().getFieldCount();
    }

    @Override
    default public void clear() {
        getInventory().clear();
    }

    @Override
    @Nonnull
    default public String getName() {
        return getInventory().getName();
    }

    @Override
    default public boolean hasCustomName() {
        return getInventory().hasCustomName();
    }

    @Override
    @Nonnull
    default public ITextComponent getDisplayName() {
        return getInventory().getDisplayName();
    }

    @Override
    default public boolean func_191420_l(){
        return getInventory().func_191420_l();
    }

}
