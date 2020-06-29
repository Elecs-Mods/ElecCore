package elec332.core.inventory.window;

import com.mojang.datafixers.util.Pair;
import elec332.core.inventory.widget.slot.WidgetSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 24-3-2017.
 */
class WrappedWidgetSlot extends WidgetSlot {

    WrappedWidgetSlot(Slot slot) {
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
    public ItemStack onTake(PlayerEntity player, @Nonnull ItemStack stack) {
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

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount) {
        return slot.decrStackSize(amount);
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return slot.canTakeStack(playerIn);
    }

    @Override
    public boolean isEnabled() {
        return slot.isEnabled();
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getBackground() {
        return slot.func_225517_c_();
    }

    @Override
    public void setBackground(@Nonnull ResourceLocation atlas, @Nonnull ResourceLocation sprite) {
        slot.setBackground(atlas, sprite);
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
