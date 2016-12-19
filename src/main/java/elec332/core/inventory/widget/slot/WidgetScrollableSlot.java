package elec332.core.inventory.widget.slot;

import net.minecraftforge.items.IItemHandler;

/**
 * Created by Elec332 on 3-12-2016.
 */
public class WidgetScrollableSlot extends WidgetSlot {

    public WidgetScrollableSlot(IItemHandler inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.index = index;
    }

    private int index;

    public void setSlotIndex(int index) {
        this.index = index;
    }

    @Override
    public int getSlotIndex() {
        return this.index;
    }

}
