package elec332.core.util;

import elec332.core.mcabstractionlayer.impl.MCAbstractedCreativeTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractCreativeTab extends CreativeTabs {

    public static AbstractCreativeTab create(String label, ItemStack icon){
        return create(getNextID(), label, icon);
    }

    public static AbstractCreativeTab create(String label, Supplier<ItemStack> icon){
        return create(getNextID(), label, icon);
    }

    @Nonnull
    public static AbstractCreativeTab create(int index, String label, ItemStack icon){
        return create(index, label, () -> icon);
    }

    @Nonnull
    public static AbstractCreativeTab create(int index, String label, Supplier<ItemStack> icon){
        return (AbstractCreativeTab) createTab(index, label, icon);
    }

    @Nonnull
    private static CreativeTabs createTab(int index, String label, Supplier<ItemStack> icon){
        return new MCAbstractedCreativeTab(index, label, icon);
    }

    public AbstractCreativeTab(int index, String label) {
        super(index, label);
    }

    public AbstractCreativeTab(String label) {
        super(label);
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    protected abstract ItemStack getDisplayStack();

}
