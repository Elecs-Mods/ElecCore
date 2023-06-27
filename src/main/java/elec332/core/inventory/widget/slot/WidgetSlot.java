package elec332.core.inventory.widget.slot;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.widget.Widget;
import elec332.core.inventory.window.Window;
import elec332.core.util.ItemStackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 28-11-2016.
 */
public class WidgetSlot extends Widget {

    public WidgetSlot(IItemHandler inventory, int index, int x, int y) {
        super(x, y, 0, 0, 16, 16);
        this.inventory = inventory;
        this.slotIndex = index;
        this.changeListeners = Lists.newArrayList();
        this.ghostBackground = this.skipBackground = false;
    }

    private final List<Consumer<WidgetSlot>> changeListeners;
    private final IItemHandler inventory;
    private final int slotIndex;
    private boolean ghostBackground, skipBackground;

    public WidgetSlot addChangeListener(Consumer<WidgetSlot> listener) {
        changeListeners.add(listener);
        return this;
    }

    public WidgetSlot setGhostBackground() {
        ghostBackground = true;
        return this;
    }

    public WidgetSlot setSkipBackground() {
        skipBackground = true;
        return this;
    }

    protected void notifyChangeListeners() {
        changeListeners.forEach(l -> l.accept(this));
    }

    @Override
    public void draw(Window window, @Nonnull MatrixStack matrixStack, int guiX, int guiY, double mouseX, double mouseY, float partialTicks) {
        if (skipBackground) {
            return;
        }
        if (ghostBackground) {
            bindTexture(Window.DEFAULT_BACKGROUND);
            GuiDraw.drawTexturedModalRect(guiX + x - 1, guiY + y - 1, 198, 0, 18, 18);
        } else {
            drawHollow(guiX, guiY, -1, -1, 18, 18);
        }
    }

    @Override
    public void onWindowClosed(PlayerEntity player) {
        //inventory.closeInventory(player);
    }

    public void onSlotChange(ItemStack newStack, ItemStack oldStack) {
        int i = oldStack.getCount() - newStack.getCount();
        if (i > 0) {
            this.onCrafting(oldStack, i);
        }
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    public void onCrafting(ItemStack stack, int amount) {
    }

    public void onSwapCraft(int slot) {
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    public void onCrafting(ItemStack stack) {
    }

    public boolean canMergeSlot(ItemStack stack) {
        return true;
    }

    @Nonnull
    public ItemStack onTake(PlayerEntity player, @Nonnull ItemStack stack) {
        this.onSlotChanged();
        return stack;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean isItemValid(ItemStack stack) {
        if (isHidden() || !ItemStackHelper.isStackValid(stack)) {
            return false;
        }
        IItemHandler handler = this.getInventory();
        ItemStack remainder;
        if (handler instanceof IItemHandlerModifiable) {
            IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable) handler;
            ItemStack currentStack = handlerModifiable.getStackInSlot(getSlotIndex());

            handlerModifiable.setStackInSlot(getSlotIndex(), ItemStackHelper.NULL_STACK);

            remainder = handlerModifiable.insertItem(getSlotIndex(), stack, true);

            handlerModifiable.setStackInSlot(getSlotIndex(), currentStack);
        } else {
            remainder = handler.insertItem(getSlotIndex(), stack, true);
        }
        return !ItemStackHelper.isStackValid(remainder) || remainder.getCount() < stack.getCount();
    }

    /**
     * Helper fnct to get the stack in the slot.
     */
    @Nonnull
    public ItemStack getStack() {
        return isHidden() ? ItemStackHelper.NULL_STACK : this.getInventory().getStackInSlot(this.getSlotIndex());
    }

    /**
     * Returns if this slot contains a stack.
     */
    public boolean getHasStack() {
        return !isHidden() && ItemStackHelper.isStackValid(this.getStack());
    }

    /**
     * Helper method to put a stack in the slot.
     */
    public void putStack(@Nonnull ItemStack stack) {
        ((IItemHandlerModifiable) this.getInventory()).setStackInSlot(getSlotIndex(), stack);
        this.onSlotChanged();
    }

    /**
     * Called when the stack in a Slot changes
     */
    public void onSlotChanged() {
        notifyChangeListeners();
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit() {
        return 64;
    }

    public int getItemStackLimit(ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);
        IItemHandler handler = this.getInventory();
        ItemStack currentStack = handler.getStackInSlot(getSlotIndex());
        if (handler instanceof IItemHandlerModifiable) {
            IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable) handler;
            handlerModifiable.setStackInSlot(getSlotIndex(), ItemStackHelper.NULL_STACK);
            ItemStack remainder = handlerModifiable.insertItem(getSlotIndex(), maxAdd, true);
            handlerModifiable.setStackInSlot(getSlotIndex(), currentStack);
            return maxInput - (ItemStackHelper.isStackValid(remainder) ? remainder.getCount() : 0);
        } else {
            ItemStack remainder = handler.insertItem(getSlotIndex(), maxAdd, true);
            int current = !ItemStackHelper.isStackValid(currentStack) ? 0 : currentStack.getCount();
            int added = maxInput - (ItemStackHelper.isStackValid(remainder) ? remainder.getCount() : 0);
            return current + added;
        }
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Nonnull
    public ItemStack decrStackSize(int amount) {
        return this.getInventory().extractItem(getSlotIndex(), amount, false);
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(PlayerEntity playerIn) {
        return !isHidden() && ItemStackHelper.isStackValid(this.inventory.extractItem(getSlotIndex(), 1, true));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isEnabled() {
        return !isHidden();
    }

    private Pair<ResourceLocation, ResourceLocation> backgroundPair;

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Pair<ResourceLocation, ResourceLocation> getBackground() {
        return backgroundPair;
    }

    /**
     * Sets the background atlas and sprite location.
     *
     * @param atlas  The atlas name
     * @param sprite The sprite located on that atlas.
     */
    public void setBackground(@Nonnull ResourceLocation atlas, @Nonnull ResourceLocation sprite) {
        this.backgroundPair = Pair.of(atlas, sprite);
    }

    /**
     * Retrieves the index in the inventory for this slot, this value should typically not
     * be used, but can be useful for some occasions.
     *
     * @return Index in associated inventory for this slot.
     */
    public int getSlotIndex() {
        return slotIndex;
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    /**
     * Checks if the other slot is in the same inventory, by comparing the inventory reference.
     *
     * @param other the other inventory
     * @return true if the other slot is in the same inventory
     */
    public boolean isSameInventory(WidgetSlot other) {
        return other.getInventory() == this.getInventory();
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public ToolTip getToolTip(double mouseX, double mouseY) {
        ItemStack stack = getStack();
        if (ItemStackHelper.isStackValid(stack)) {
            return new ToolTip(GuiDraw.getItemToolTip(stack));
        }
        return null;
    }

}
