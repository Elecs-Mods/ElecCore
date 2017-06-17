package elec332.core.inventory;

import com.google.common.collect.Lists;
import elec332.core.inventory.slot.SlotOutput;
import elec332.core.inventory.widget.IWidget;
import elec332.core.util.InventoryHelper;
import elec332.core.util.ItemStackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 4-4-2015.
 */
public class BaseContainer extends Container implements IWidgetContainer{

    BaseContainer(EntityPlayer player, int offset){
        this(player);
        this.offset = offset;
    }

    BaseContainer(EntityPlayer player){
        //this.tileEntity = tile;
        this.thePlayer = player;
        this.offset = 0;
        this.widgets = Lists.newArrayList();
    }

    //private BaseTileWithInventory tileEntity;
    protected final EntityPlayer thePlayer;
    protected final List<IWidget> widgets;
    private int offset;
    private int hotBarFactor = 0;
    private int playerInvIndexStart = 0;
    private int playerInvIndexStop = 0;

    public List<IWidget> getWidgets(){
        return this.widgets;
    }

    @Override
    public <W extends IWidget> W addWidget(W widget) {
        widget.setContainer(this);
        widget.setID(widgets.size());
        for (Object obj : listeners) {
            throw new RuntimeException();//widget.initWidget((IContainerListener) obj);
        }
        this.widgets.add(widget);
        return widget;
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
    public void addListener(IContainerListener iCrafting) {
        for (IWidget widget : widgets)
            throw new RuntimeException();//widget.initWidget(iCrafting);
        super.addListener(iCrafting);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IWidget widget : widgets){
            throw new RuntimeException();//widget.detectAndSendChanges(listeners);
        }
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack itemstack = ItemStackHelper.NULL_STACK;
        Slot slot = getSlot(slotID);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (slot instanceof SlotOutput) {
                if (!this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop, true)) {
                    return ItemStackHelper.NULL_STACK;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (slotID >= playerInvIndexStart && slotID <= playerInvIndexStop) {
                for (int i = 0; i < playerInvIndexStart; i++) {
                    Slot slot1 = getSlot(i);
                    if (slot1.isItemValid(itemstack1)) {
                        if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
                            if (itemstack1.stackSize < 1)
                                putStackInSlot(slotID, ItemStackHelper.NULL_STACK);  //Workaround for ghost itemstacks
                            return ItemStackHelper.NULL_STACK;
                        }
                    }
                }
                if (slotID >= playerInvIndexStart && slotID < playerInvIndexStop-9) {
                    if (!this.mergeItemStack(itemstack1, playerInvIndexStop-9, playerInvIndexStop, false)) {
                        if (itemstack1.stackSize < 1)
                            putStackInSlot(slotID, ItemStackHelper.NULL_STACK);  //Workaround for ghost itemstacks
                        return ItemStackHelper.NULL_STACK;
                    }
                } else if (slotID >= playerInvIndexStop-9 && slotID < playerInvIndexStop && !this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop-9, false)) {
                    if (itemstack1.stackSize < 1)
                        putStackInSlot(slotID, ItemStackHelper.NULL_STACK);  //Workaround for ghost itemstacks
                    return ItemStackHelper.NULL_STACK;
                }
            } else if (!this.mergeItemStack(itemstack1, playerInvIndexStart, playerInvIndexStop, false)) {
                return ItemStackHelper.NULL_STACK;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(ItemStackHelper.NULL_STACK);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return ItemStackHelper.NULL_STACK;
            }

            slot.onTake(player, itemstack1);
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
