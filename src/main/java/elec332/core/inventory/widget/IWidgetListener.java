package elec332.core.inventory.widget;

import elec332.core.inventory.window.IWindowListener;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Elec332 on 29-11-2016.
 */
public interface IWidgetListener {

    /**
     * update the crafting window inventory with the items in the list
     */
    void updateCraftingInventory(List<ItemStack> itemsList);

    /**
     * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
     * contents of that slot.
     */
    void sendSlotContents(int slotInd, ItemStack stack);

    /**
     * Sends two ints to the client-side Container. Used for furnace burning time, smelting progress, brewing progress,
     * and enchanting level. Normally the first int identifies which variable to update, and the second contains the new
     * value. Both are truncated to shorts in non-local SMP.
     */
    void sendProgressBarUpdate(int newValue);

    IWindowListener getWindowListener();

}
