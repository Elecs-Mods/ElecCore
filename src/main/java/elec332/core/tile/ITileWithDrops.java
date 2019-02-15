package elec332.core.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Created by Elec332 on 7-2-2019
 *
 * Created because something alike is due to be added to forge, and I need it :)
 */
public interface ITileWithDrops {

    public void getDrops(NonNullList<ItemStack> items, int fortune);

}
