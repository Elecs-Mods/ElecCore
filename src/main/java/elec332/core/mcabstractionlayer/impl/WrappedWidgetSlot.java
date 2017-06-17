package elec332.core.mcabstractionlayer.impl;

import elec332.core.inventory.widget.slot.WidgetSlot;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 24-3-2017.
 */
public class WrappedWidgetSlot extends WidgetSlot {

    public WrappedWidgetSlot(Slot slot){
        super(null, slot.getSlotIndex(), slot.xPos, slot.yPos);
        this.slot = slot;
    }

    private final Slot slot;

    @Override
    public void onSlotChange(ItemStack newStack, ItemStack oldStack) {
        slot.onSlotChange(newStack, oldStack);
    }

    @Override
    public void onCrafting(ItemStack stack, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onCrafting(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onSwapCraft(int slot) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public ItemStack onTake(EntityPlayer player, @Nonnull ItemStack stack) {
        slot.onTake(player, stack);
        return stack;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return slot.isItemValid(stack);
    }

    @Override
    public boolean getHasStack() {
        return slot.getHasStack();
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        slot.putStack(stack);
    }

    @Override
    public void onSlotChanged() {
        slot.onSlotChanged();
    }

    @Override
    public int getSlotStackLimit() {
        return slot.getSlotStackLimit();
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return slot.getItemStackLimit(stack);
    }

    @Nullable
    @Override
    public String getSlotTexture() {
        return slot.getSlotTexture();
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount) {
        return slot.decrStackSize(amount);
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn) {
        return slot.isHere(inv, slotIn);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return slot.canTakeStack(playerIn);
    }

    @Override
    public boolean canBeHovered() {
        return slot.canBeHovered();
    }

    @Nonnull
    @Override
    public ResourceLocation getBackgroundLocation() {
        return slot.getBackgroundLocation();
    }

    @Override
    public void setBackgroundLocation(@Nonnull ResourceLocation texture) {
        slot.setBackgroundLocation(texture);
    }

    @Override
    public void setBackgroundName(@Nonnull String name) {
        slot.setBackgroundName(name);
    }

    @Override
    public TextureAtlasSprite getBackgroundSprite() {
        return slot.getBackgroundSprite();
    }

    @Nonnull
    @Override
    public TextureMap getBackgroundMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSlotIndex() {
        return slot.getSlotIndex();
    }

    @Override
    public boolean isSameInventory(WidgetSlot other) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public ItemStack getStack() {
        return slot.getStack();
    }

    @Override
    public IItemHandler getInventory() {
        return new InvWrapper(slot.inventory);
    }

}
