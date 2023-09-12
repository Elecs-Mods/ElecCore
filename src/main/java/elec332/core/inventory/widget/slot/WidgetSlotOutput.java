package elec332.core.inventory.widget.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by Elec332 on 17-12-2016.
 */
public class WidgetSlotOutput extends WidgetSlot {

    public WidgetSlotOutput(IItemHandler inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

}
