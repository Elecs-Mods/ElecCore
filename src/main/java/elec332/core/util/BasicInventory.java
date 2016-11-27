package elec332.core.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 4-5-2015.
 */
public class BasicInventory implements IInventory {

    public static BasicInventory copyOf(IInventory inventory){
        BasicInventory ret = new BasicInventory(inventory.getName(), inventory.getSizeInventory());
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ret.setInventorySlotContents(i, ItemStackHelper.copyItemStack(inventory.getStackInSlot(i)));
        }
        return ret;
    }

    public BasicInventory(String s, int i, TileEntity tile){
        this(s, i);
        this.tile = tile;
    }

    public BasicInventory(String name, int slotsCount) {
        this.inventoryTitle = name;
        this.slotsCount = slotsCount;
        this.inventoryContents = InventoryHelper.newItemStackList(slotsCount);
    }

    private String inventoryTitle;
    private int slotsCount;
    protected MinecraftList<ItemStack> inventoryContents;
    private TileEntity tile;

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slotID) {
        return slotID >= 0 && slotID < this.inventoryContents.size() ? this.inventoryContents.get(slotID) : ItemStackHelper.NULL_STACK;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int slotID, int size) {
        ItemStack itemstack = getStackInSlot(slotID);
        if (ItemStackHelper.isStackValid(itemstack)) {
            if (itemstack.stackSize <= size) {
                setInventorySlotContents(slotID, ItemStackHelper.NULL_STACK);
                this.markDirty();
                return itemstack;
            } else {
                itemstack = itemstack.splitStack(size);
                if (getStackInSlot(slotID).stackSize == 0) {
                    setInventorySlotContents(slotID, ItemStackHelper.NULL_STACK);
                }
                this.markDirty();
                return itemstack;
            }
        } else {
            return ItemStackHelper.NULL_STACK;
        }
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int slotID) {
        return ItemStackHelper.getAndRemove(inventoryContents, slotID);
    }

    @Override
    public void setInventorySlotContents(int slotID, @Nonnull ItemStack stack) {
        if (!ItemStackHelper.isStackValid(stack)){
            inventoryContents.set(slotID, ItemStackHelper.NULL_STACK);
            return;
        }
        if (isItemValidForSlot(slotID, stack)) {
            inventoryContents.set(slotID, stack);
            if (stack.stackSize > this.getInventoryStackLimit()) {
                stack.stackSize = this.getInventoryStackLimit();
            }
            this.markDirty();
        }
    }

    public boolean canAddItemStackFully(ItemStack itemStack, int i, boolean ignoreNBT){
        if (!isItemValidForSlot(i, itemStack))
            return false;
        ItemStack stackInSlot = getStackInSlot(i);
        if (!ItemStackHelper.isStackValid(stackInSlot))
            return true;
        if (itemStack.getItem() == stackInSlot.getItem() && itemStack.getItemDamage() == stackInSlot.getItemDamage()){
            int j = itemStack.stackSize+stackInSlot.stackSize;
            if (j > getInventoryStackLimit())
                return false;
            if (!itemStack.hasTagCompound() && !stackInSlot.hasTagCompound() || ignoreNBT)
                return true;
            if (itemStack.hasTagCompound() && stackInSlot.hasTagCompound() && stackInSlot.getTagCompound().equals(itemStack.getTagCompound()))
                return true;
        }
        return false;
    }

    @Override
    public int getSizeInventory() {
        return this.slotsCount;
    }

    @Override //TODO: Whats this?
    public boolean func_191420_l() {
        return false;
    }

    @Override
    @Nonnull
    public String getName() {
        return this.inventoryTitle;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        if (tile != null) {
            tile.markDirty();
        }
    }

    @Override
    public boolean isUseableByPlayer(@Nonnull EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {}

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int id, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        inventoryContents.clear();
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.inventoryContents = InventoryHelper.newItemStackList(getSizeInventory());
        InventoryHelper.readItemsFromNBT(compound, this.inventoryContents);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return InventoryHelper.writeItemsToNBT(compound, inventoryContents);
    }

    public void copyTo(IInventory inv){
        if (inv.getSizeInventory() < getSizeInventory()){
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = ItemStackHelper.NULL_STACK;
            if (i < getSizeInventory()){
                stack = ItemStackHelper.copyItemStack(getStackInSlot(i));
            }
            inv.setInventorySlotContents(i, stack);
        }
    }

}
