package elec332.core.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-12-2018
 */
public class ItemMethods {

    @SuppressWarnings("all")
    public static <I extends Item & IAbstractItem> String createUnlocalizedName(I item) {
        if (item instanceof ItemBlock) {
            return ((ItemBlock) item).getBlock().getUnlocalizedName();
        }
        return "item." + item.getRegistryName().toString().replace(":", ".").toLowerCase();
    }

    @Nonnull
    public static <I extends Item & IAbstractItem> String getUnlocalizedName(ItemStack stack, I item) {
        if (item.getHasSubtypes()) {
            return item.getUnlocalizedName() + "." + item.getVariantName(stack);
        }
        return item.getUnlocalizedName();
    }

}
