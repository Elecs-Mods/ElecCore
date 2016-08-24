package elec332.core.compat.forestry.bee;

import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IHiveFrame;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 21-8-2016.
 */
public interface IDefaultHiveFrame extends IHiveFrame {

    @Override
    default public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
        frame.setItemDamage(frame.getItemDamage() + wear);
        if (frame.getItemDamage() >= frame.getMaxDamage()) {
            frame = null;
        }
        return frame;
    }

}
