package elec332.core.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractCreativeTab extends CreativeTabs {

    public static AbstractCreativeTab create(String label, ItemStack icon) {
        return create(getNextID(), label, icon);
    }

    public static AbstractCreativeTab create(String label, Supplier<ItemStack> icon) {
        return create(getNextID(), label, icon);
    }

    @Nonnull
    public static AbstractCreativeTab create(int index, String label, ItemStack icon) {
        return create(index, label, () -> icon);
    }

    @Nonnull
    public static AbstractCreativeTab create(int index, String label, Supplier<ItemStack> icon) {
        return (AbstractCreativeTab) createTab(index, label, icon);
    }

    @Nonnull
    private static CreativeTabs createTab(int index, String label, Supplier<ItemStack> icon) {
        return new AbstractCreativeTab(index, label, icon) {
        };
    }

    public AbstractCreativeTab(String label, Supplier<ItemStack> icon) {
        this(getNextID(), label, icon);
    }

    public AbstractCreativeTab(int index, String label, Supplier<ItemStack> icon) {
        super(index, label);
        this.icon = icon;
        initStack();
    }

    @SideOnly(Side.CLIENT)
    private ItemStack clientStack;
    private final Supplier<ItemStack> icon;

    private void initStack() {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            clientStack = icon.get();
        }
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return this.clientStack;
    }

}
