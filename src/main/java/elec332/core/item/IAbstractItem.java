package elec332.core.item;

import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 29-12-2018
 */
public interface IAbstractItem {

    default public String getVariantName(ItemStack stack) {
        return Integer.toString(stack.getItemDamage());
    }

}
