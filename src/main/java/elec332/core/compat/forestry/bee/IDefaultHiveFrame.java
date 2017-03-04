package elec332.core.compat.forestry.bee;

import elec332.core.util.ItemStackHelper;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IHiveFrame;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 21-8-2016.
 */
public interface IDefaultHiveFrame extends IHiveFrame {

    @Override
    @Nonnull
    default public ItemStack frameUsed(@Nonnull IBeeHousing housing, @Nonnull ItemStack frame, @Nonnull IBee queen, int wear) {
        frame.setItemDamage(frame.getItemDamage() + wear);
        if (frame.getItemDamage() >= frame.getMaxDamage()) {
            frame = ItemStackHelper.NULL_STACK;
        }
        return frame;
    }

}
