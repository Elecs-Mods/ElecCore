package elec332.core.baseclasses.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Created by Elec332 on 30-3-2015.
 */
public abstract class BaseTileWithInventory extends TileBase implements IInventory {

    public BaseTileWithInventory(int size){
        super();
        this.size = size;
        this.inventoryContent= new ItemStack[size];
    }

    private ItemStack[] inventoryContent;
    private int size;
    protected String customInventoryName;

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return this.size;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int p_70301_1_)
    {
        return this.inventoryContent[p_70301_1_];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of item and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
    {
        if (this.inventoryContent[p_70298_1_] != null)
        {
            ItemStack itemstack;

            if (this.inventoryContent[p_70298_1_].stackSize <= p_70298_2_)
            {
                itemstack = this.inventoryContent[p_70298_1_];
                this.inventoryContent[p_70298_1_] = null;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = this.inventoryContent[p_70298_1_].splitStack(p_70298_2_);

                if (this.inventoryContent[p_70298_1_].stackSize == 0)
                {
                    this.inventoryContent[p_70298_1_] = null;
                }

                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        if (this.inventoryContent[p_70304_1_] != null) {
            ItemStack itemstack = this.inventoryContent[p_70304_1_];
            this.inventoryContent[p_70304_1_] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
        this.inventoryContent[p_70299_1_] = p_70299_2_;
        if (p_70299_2_ != null && p_70299_2_.stackSize > this.getInventoryStackLimit()) {
            p_70299_2_.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    /**
     * Returns the name of the inventory
     */
    @Override
    public String getInventoryName() {
        return this.hasCustomInventoryName()?this.customInventoryName:standardInventoryName();
    }

    protected abstract String standardInventoryName();

    public void setCustomInventoryName(String newName) {
        this.customInventoryName = newName;
    }

    /**
     * Returns if the inventory is named
     */
    public boolean hasCustomInventoryName() {
        return this.customInventoryName != null;
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.inventoryContent = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
            int j = tag.getByte("Slot") & 255;
            if (j >= 0 && j < this.inventoryContent.length) {
                this.inventoryContent[j] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
        if (compound.hasKey("CustomName", 8)) {
            this.customInventoryName = compound.getString("CustomName");
        }
    }

    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.inventoryContent.length; ++i) {
            if (this.inventoryContent[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                this.inventoryContent[i].writeToNBT(tag);
                nbttaglist.appendTag(tag);
            }
        }
        compound.setTag("Items", nbttaglist);
        if (this.hasCustomInventoryName()) {
            compound.setString("CustomName", this.customInventoryName);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && p_70300_1_.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return true;
    }
}
