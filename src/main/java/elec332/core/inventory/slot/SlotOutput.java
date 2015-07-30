package elec332.core.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 5-5-2015.
 */
public class SlotOutput extends Slot {
    public SlotOutput(IInventory inventory, int index, int x, int z) {
        super(inventory, index, x, z);
    }

    @Override
    public boolean isItemValid(ItemStack p_75214_1_) {
        return false;
    }
}
