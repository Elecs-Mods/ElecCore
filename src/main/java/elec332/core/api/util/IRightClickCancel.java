package elec332.core.api.util;

import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 30-4-2015.
 */
public interface IRightClickCancel {

    default public boolean cancelInteraction(ItemStack stack) {
        return true;
    }

}
