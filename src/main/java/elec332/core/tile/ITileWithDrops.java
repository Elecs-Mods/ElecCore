package elec332.core.tile;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Created by Elec332 on 7-2-2019
 * <p>
 * Created because something alike is due to be added to forge, and I need it :)
 */
public interface ITileWithDrops {

    public void getDrops(List<ItemStack> items, int fortune);

}
