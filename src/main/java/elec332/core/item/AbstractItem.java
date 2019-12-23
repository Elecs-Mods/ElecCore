package elec332.core.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 21-8-2016.
 */
public abstract class AbstractItem extends Item implements IAbstractItem {

    public AbstractItem(Properties itemBuilder) {
        super(itemBuilder);
    }

    private String unlocalizedName;

    @Nonnull
    @Override
    public String getDefaultTranslationKey() {
        if (this.unlocalizedName == null) {
            unlocalizedName = ItemMethods.createUnlocalizedName(this);
        }
        return unlocalizedName;
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        return ItemMethods.getUnlocalizedName(stack, this);
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        super.fillItemGroup(group, items);
    }

}
