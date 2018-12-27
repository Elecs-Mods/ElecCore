package elec332.core.inventory.widget.slot;

import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.inventory.tooltip.ToolTip;
import elec332.core.inventory.widget.Widget;
import elec332.core.inventory.window.Window;
import elec332.core.util.ItemStackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 28-11-2016.
 */
@SuppressWarnings("WeakerAccess")
public class WidgetSlot extends Widget {

    public WidgetSlot(IItemHandler inventory, int index, int x, int y) {
        super(x, y, 0, 0, 16, 16);
        this.inventory = inventory;
        this.slotIndex = index;
    }

    @Override
    public void draw(Window window, int guiX, int guiY, int mouseX, int mouseY) {
        RenderHelper.bindTexture(Window.DEFAULT_BACKGROUND);
        GuiDraw.drawTexturedModalRect(guiX + x - 1, guiY + y - 1, 180, 0, 18, 18);
    }

    private final IItemHandler inventory;
    private final int slotIndex;

    @Override
    public void onWindowClosed(EntityPlayer player) {
        //TODO inventory.closeInventory(player);
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
    public ItemStack onTake(EntityPlayer player, @Nonnull ItemStack stack) {
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

    @Nullable
    @SideOnly(Side.CLIENT)
    public String getSlotTexture() {
        return backgroundName;
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
     * returns true if the slot exists in the given inventory and location
     */
    public boolean isHere(IInventory inv, int slotIn) {
        return !isHidden() && inv == this.inventory && slotIn == getSlotIndex();
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(EntityPlayer playerIn) {
        return !isHidden() && ItemStackHelper.isStackValid(this.inventory.extractItem(getSlotIndex(), 1, true));
    }

    /**
     * Actualy only call when we want to render the white square effect over the slots. Return always True, except for
     * the armor slot of the Donkey/Mule (we can't interact with the Undead and Skeleton horses)
     */
    @SideOnly(Side.CLIENT)
    public boolean canBeHovered() {
        return !isHidden();
    }

    private String backgroundName = null;
    private ResourceLocation backgroundLocation = null;
    private Object backgroundMap;

    /**
     * Gets the path of the texture file to use for the background image of this slot when drawing the GUI.
     *
     * @return The resource location for the background image
     */
    @SideOnly(Side.CLIENT)
    @Nonnull
    public ResourceLocation getBackgroundLocation() {
        return (backgroundLocation == null ? net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE : backgroundLocation);
    }

    /**
     * Sets the texture file to use for the background image of the slot when it's empty.
     *
     * @param texture the resourcelocation for the texture
     */
    @SideOnly(Side.CLIENT)
    public void setBackgroundLocation(@Nonnull ResourceLocation texture) {
        this.backgroundLocation = texture;
    }

    /**
     * Sets which icon index to use as the background image of the slot when it's empty.
     *
     * @param name The icon to use, null for none
     */
    public void setBackgroundName(@Nonnull String name) {
        this.backgroundName = name;
    }

    @SideOnly(Side.CLIENT)
    public net.minecraft.client.renderer.texture.TextureAtlasSprite getBackgroundSprite() {
        String name = getSlotTexture();
        return name == null ? null : getBackgroundMap().getAtlasSprite(name);
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.renderer.texture.TextureMap getBackgroundMap() {
        if (backgroundMap == null) {
            backgroundMap = net.minecraft.client.Minecraft.getMinecraft().getTextureMapBlocks();
        }
        return (net.minecraft.client.renderer.texture.TextureMap) backgroundMap;
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
    @SideOnly(Side.CLIENT)
    public ToolTip getToolTip(int mouseX, int mouseY) {
        ItemStack stack = getStack();
        if (ItemStackHelper.isStackValid(stack)) {
            return new ToolTip(GuiDraw.getItemToolTip(stack));
        }
        return null;
    }

}
