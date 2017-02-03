package elec332.core.util;

import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Created by Elec332 on 30-1-2017.
 */
public interface IElecItemHandler extends IItemHandlerModifiable {

    public int getSlotLimit(int slot);

}
