package elec332.core.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 21-8-2016.
 */
public abstract class AbstractItem extends Item implements IAbstractItem {

    private String unlocalizedName;

    @Nonnull
    @Override
    public String getUnlocalizedName() {
        if (this.unlocalizedName == null) {
            unlocalizedName = ItemMethods.createUnlocalizedName(this);
        }
        return unlocalizedName;
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return ItemMethods.getUnlocalizedName(stack, this);
    }

}
