package elec332.core.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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

    private ItemStack stack;

    @Override
    @Nonnull
    public final Item getTabIconItem() {
        return getCached().getItem();
    }

    @Override
    public int getIconItemDamage() {
        return getCached().getItemDamage();
    }

    private ItemStack getCached(){
        if (stack == null){
            stack = getDisplayStack();
        }
        return stack;
    }

    @Nonnull
    protected abstract ItemStack getDisplayStack();

}
