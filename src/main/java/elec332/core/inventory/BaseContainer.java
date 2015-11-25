package elec332.core.inventory;

import com.google.common.collect.Lists;
import elec332.core.inventory.slot.SlotOutput;
import elec332.core.inventory.widget.Widget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Elec332 on 4-4-2015.
 */
public class BaseContainer extends Container implements IWidgetContainer{

    public BaseContainer(EntityPlayer player, int offset){
        this(player);
        this.offset = offset;
    }

    public BaseContainer(EntityPlayer player){
        //this.tileEntity = tile;
        this.thePlayer = player;
        this.offset = 0;
        this.widgets = Lists.newArrayList();
    }

    //private BaseTileWithInventory tileEntity;
    protected final EntityPlayer thePlayer;
    protected final List<Widget> widgets;
    private int offset;
    private int hotBarFactor = 0;
    private int playerInvIndexStart = 0;
    private int playerInvIndexStop = 0;

    public List<Widget> getWidgets(){
        return this.widgets;
    }

    @Override
    public void addWidget(Widget widget) {
        widget.setContainer(this);
        widget.setID(widgets.size());
        for (Object obj : crafters)
            widget.initWidget((ICrafting)obj);
        this.widgets.add(widget);
    }

    public void addPlayerInventoryToContainer(){
        this.playerInvIndexStart = inventorySlots.size();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(thePlayer.inventory, j + i * 9 + 9, 8 + j * 18, (84 + this.offset) + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(thePlayer.inventory, i, 8 + i * 18, 142 + this.offset + hotBarFactor()));
        }
        this.playerInvIndexStop = inventorySlots.size();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void onCraftGuiOpened(ICrafting iCrafting) {
        for (Widget widget : widgets)
            widget.initWidget(iCrafting);
        super.onCraftGuiOpened(iCrafting);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Widget widget : widgets){
            widget.detectAndSendChanges(crafters);
        }
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
            } else if (slotID >= playerInvIndexStart && slotID <= playerInvIndexStop) {
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
            } else if (!this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop, false)) {
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
    public Slot addSlotToContainer(Slot slot) {
        return super.addSlotToContainer(slot);
    }

    public void setHotBarFactor(int hotBarFactor) {
        this.hotBarFactor = hotBarFactor;
    }

    protected int hotBarFactor(){
        return hotBarFactor;
    }

    @Override
    public void updateProgressBar(int id, int value) {
        widgets.get(id).updateProgressbar(value);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return //this.tileEntity.isUseableByPlayer(player);
        true;
    }
}
