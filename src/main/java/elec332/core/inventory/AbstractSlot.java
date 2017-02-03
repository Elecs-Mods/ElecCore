package elec332.core.inventory;

import elec332.abstraction.impl.MCAbstractedSlot;
import elec332.abstraction.object.IAbstractedSlot;
import net.minecraft.inventory.IInventory;

/**
 * Created by Elec332 on 30-1-2017.
 */
public abstract class AbstractSlot extends MCAbstractedSlot implements IAbstractedSlot {

    public AbstractSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

}
