package elec332.core.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractCreativeTab extends CreativeTabs {

    public AbstractCreativeTab(String label) {
        super(label);
    }

    public AbstractCreativeTab(int index, String label) {
        super(index, label);
    }

    @Override
    @Nonnull
    public final ItemStack getTabIconItem() {
        return getDisplayStack();
    }

    @Nonnull
    protected abstract ItemStack getDisplayStack();

}
