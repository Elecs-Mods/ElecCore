package elec332.core.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 27-12-2018
 */
public class ItemMethods {

    @SuppressWarnings("all")
    public static <I extends Item & IAbstractItem> String createUnlocalizedName(I item) {
        if (item instanceof BlockItem) {
            return ((BlockItem) item).getBlock().getTranslationKey();
        }
        return "item." + item.getRegistryName().toString().replace(":", ".").toLowerCase();
    }

    @Nonnull
    public static <I extends Item & IAbstractItem> String getUnlocalizedName(ItemStack stack, I item) {
        if (stack.hasTag()) {
            return item.getTranslationKey() + "." + item.getVariantName(stack);
        }
        return item.getTranslationKey();
    }

}
