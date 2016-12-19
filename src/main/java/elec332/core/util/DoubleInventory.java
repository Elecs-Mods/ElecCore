package elec332.core.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 9-7-2015.
 */
public class DoubleInventory<I1 extends IInventory, I2 extends IInventory> implements IInventory {

    public DoubleInventory(I1 inv1, I2 inv2){
        if (inv1 == null || inv2 == null)
            throw new IllegalArgumentException("Inventory cannot be null!");
        this.inventory1 = inv1;
        this.inventory2 = inv2;
        this.size1 = InventoryHelper.getMainInventorySize(inv1);
        this.size2 = InventoryHelper.getMainInventorySize(inv2);
        this.totalSize = size1 + size2;
    }

    public static <I1 extends IInventory, I2 extends IInventory> DoubleInventory<I1, I2> fromInventory(I1 i1, I2 i2, DoubleInventory<I1, I2> inventory){
        DoubleInventory<I1, I2> ret =  new DoubleInventory<I1, I2>(i1, i2);
        ret.copyFrom(inventory);
        return ret;
    }

    private I1 inventory1;
    private I2 inventory2;
    private int totalSize;
    private int size1;
    private int size2;

    @Override
    public int getSizeInventory() {
        return totalSize;
    }

    @Override
    public boolean isEmpty() {
        return inventory1.isEmpty() && inventory2.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if (i < size1) {
            return inventory1.getStackInSlot(i);
        } else {
            return inventory2.getStackInSlot(i-size1);
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot < size1) {
            return inventory1.decrStackSize(slot, amount);
        } else {
            return inventory2.decrStackSize(slot - size1, amount);
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        if (i < size1) {
            return inventory1.removeStackFromSlot(i);
        } else {
            return inventory2.removeStackFromSlot(i - size1);
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack) {
        if (i < size1) {
            inventory1.setInventorySlotContents(i, stack);
        } else {
            inventory2.setInventorySlotContents(i - size1, stack);
        }
    }

    @Override
    @Nonnull
    public String getName() {
        return inventory1.getName()+" & "+inventory2.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory1.hasCustomName() || inventory2.hasCustomName();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        inventory1.markDirty();
        inventory2.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        return inventory1.isUsableByPlayer(player) && inventory2.isUsableByPlayer(player);
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {
        inventory1.openInventory(player);
        inventory2.openInventory(player);
    }

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {
        inventory1.closeInventory(player);
        inventory2.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (slot < size1) {
            return inventory1.isItemValidForSlot(slot, stack);
        } else {
            return inventory2.isItemValidForSlot(slot - size1, stack);
        }
    }

    /**
     * New 1.8 stuff.
     */

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return inventory1.getDisplayName().appendSibling(inventory2.getDisplayName());
    }

    @Override
    public int getField(int id) {
        return id < inventory1.getFieldCount() ? inventory1.getField(id) : inventory2.getField(id - inventory1.getFieldCount());
    }

    @Override
    public void setField(int id, int value) {
        if (id < inventory1.getFieldCount()){
            inventory1.setField(id, value);
        } else {
            inventory2.setField(id - inventory1.getFieldCount(), value);
        }
    }

    @Override
    public int getFieldCount() {
        return inventory1.getFieldCount() + inventory2.getFieldCount();
    }

    @Override
    public void clear() {
        inventory1.clear();
        inventory2.clear();
    }

    public I1 getFirstInventory(){
        return inventory1;
    }

    public I2 getSecondInventory(){
        return inventory2;
    }

    public boolean addItemToInventory(ItemStack stack){
        return InventoryHelper.addItemToInventory(inventory1, stack) || InventoryHelper.addItemToInventory(inventory2, stack);
    }

    public int getFirstSlotWithItemStackNoNBT(ItemStack stack){
        int i = InventoryHelper.getFirstSlotWithItemStackNoNBT(inventory1, stack);
        if (i > -1)
            return i;

        int q = InventoryHelper.getFirstSlotWithItemStackNoNBT(inventory2, stack);
        if (q > -1)
            return q + size1;

        return -1;
    }

    public List<ItemStack> toList(){
        List<ItemStack> ret = InventoryHelper.storeContents(inventory1);
        ret.addAll(InventoryHelper.storeContents(inventory2));
        return ret;
    }

    public void fromList(List<ItemStack> list){
        if (list.size() != totalSize)
            throw new IllegalArgumentException();
        for (int i = 0; i < list.size(); i++) {
            setInventorySlotContents(i, ItemStackHelper.copyItemStack(list.get(i)));
        }
    }

    public void copyTo(I1 i1, I2 i2){
        for (int i = 0; i < size1; i++) {
            i1.setInventorySlotContents(i, ItemStackHelper.copyItemStack(inventory1.getStackInSlot(i)));
        }
        for (int i = 0; i < size2; i++) {
            i2.setInventorySlotContents(i, ItemStackHelper.copyItemStack(inventory2.getStackInSlot(i)));
        }
    }

    public void copyFrom(DoubleInventory<I1, I2> inventory){
        for (int i = 0; i < getSizeInventory(); i++) {
            setInventorySlotContents(i, ItemStackHelper.copyItemStack(inventory.getStackInSlot(i)));
        }
    }

}
