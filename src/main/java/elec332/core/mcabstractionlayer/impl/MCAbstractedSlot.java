package elec332.core.mcabstractionlayer.impl;

import elec332.core.mcabstractionlayer.object.IAbstractedSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 30-1-2017.
 */
public class MCAbstractedSlot extends Slot implements IAbstractedSlot {

    public MCAbstractedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    protected final void onSwapCraft(int p_190900_1_) {
        onSwapCraftC(p_190900_1_);
    }

    @Override
    public void onSwapCraftC(int count) {
        super.onSwapCraft(count);
    }

    @Override
    public final ItemStack onTake(EntityPlayer p_190901_1_, @Nonnull ItemStack p_190901_2_) {
        return onTakenFromSlotC(p_190901_1_, p_190901_2_);
    }

    @Nonnull
    @Override
    public ItemStack onTakenFromSlotC(EntityPlayer player, ItemStack stack) {
        return super.onTake(player, stack);
    }

}
