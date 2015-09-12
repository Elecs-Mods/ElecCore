package elec332.core.inventory;

import elec332.core.inventory.slot.SlotOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 5-5-2015.
 */
public class ContainerMachine extends BaseContainer {
    public ContainerMachine(ITileWithSlots tileWithSlots, EntityPlayer player, int offset) {
        super(player, offset);
        if (tileWithSlots != null) {
            tileWithSlots.addSlots(this);
            if (tileWithSlots instanceof IHasProgressBar)
                this.tileWithSlots = (IHasProgressBar) tileWithSlots;
        }
    }

    private IHasProgressBar tileWithSlots;
    private int lastProgressBarStatus = 0;
    private int playerInvIndexStart = 0;
    private int playerInvIndexStop = 0;

    public IHasProgressBar getTileWithProgressBar() {
        return tileWithSlots;
    }

    /*@Override
    public void addPlayerInventoryToContainer() {
        this.playerInvIndexStart = inventorySlots.size();
        super.addPlayerInventoryToContainer();
        this.playerInvIndexStop = inventorySlots.size();
    }

    @Override
    public void addCraftingToCrafters(ICrafting iCrafting) {
        super.addCraftingToCrafters(iCrafting);
        //if (tileWithSlots != null)
        //    iCrafting.sendProgressBarUpdate(this, 0, tileWithSlots.getProgress());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        /*if (tileWithSlots != null) {
            for (Object obj : this.crafters) {
                ICrafting iCrafting = (ICrafting) obj;
                if (tileWithSlots.getProgress() != lastProgressBarStatus) {
                    iCrafting.sendProgressBarUpdate(this, 0, tileWithSlots.getProgress());
                }
            }
            this.lastProgressBarStatus = tileWithSlots.getProgress();
        }*//*
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack itemstack = null;
        Slot slot = getSlot(slotID);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (slot instanceof SlotOutput) {
                if (!this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop, true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (slotID >= playerInvIndexStart) {
                for (int i = 0; i < playerInvIndexStart; i++) {
                    Slot slot1 = getSlot(i);
                    if (slot1.isItemValid(itemstack1)) {
                        if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
                            if (itemstack1.stackSize < 1)
                                putStackInSlot(slotID, null);  //Workaround for ghost itemstacks
                            return null;
                        }
                    }
                }
                if (slotID >= playerInvIndexStart && slotID < playerInvIndexStop-9) {
                    if (!this.mergeItemStack(itemstack1, playerInvIndexStop-9, playerInvIndexStop, false)) {
                        if (itemstack1.stackSize < 1)
                            putStackInSlot(slotID, null);  //Workaround for ghost itemstacks
                        return null;
                    }
                } else if (slotID >= playerInvIndexStop-9 && slotID < playerInvIndexStop && !this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop-9, false)) {
                    if (itemstack1.stackSize < 1)
                        putStackInSlot(slotID, null);  //Workaround for ghost itemstacks
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public void updateProgressBar(int id, int value) {
        super.updateProgressBar(id, value);
        //if (id == 0){
         //   if (tileWithSlots != null)
        //        tileWithSlots.setProgress(value);
        //}
    }*/
}
