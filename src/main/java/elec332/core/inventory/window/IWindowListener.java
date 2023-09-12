package elec332.core.inventory.window;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Created by Elec332 on 17-12-2016.
 */
public interface IWindowListener {

    /**
     * update the crafting window inventory with the items in the list
     */
    public void updateCraftingInventory(List<ItemStack> itemsList);

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot.
     */
    public void sendSlotContents(int slotInd, ItemStack stack);

    /**
     * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
     * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
     * value. Both are truncated to shorts in non-local SMP.
     */
    public void sendProgressBarUpdate(int id, int newValue);

    public Object getActualListener();

}
