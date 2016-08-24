package elec332.core.item;

import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 21-8-2016.
 */
public interface IColoredItem {

    default public int getColorFromItemStack(ItemStack stack, int tintIndex){
        return -1;
    }

}
