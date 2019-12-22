package elec332.core.util;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractCreativeTab extends ItemGroup {

    public static AbstractCreativeTab create(String label, ItemStack icon) {
        return create(GROUPS.length, label, icon);
    }

    public static AbstractCreativeTab create(String label, Supplier<ItemStack> icon) {
        return create(GROUPS.length, label, icon);
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
    private static ItemGroup createTab(int index, String label, Supplier<ItemStack> icon) {
        return new AbstractCreativeTab(index, label, icon) {

        };
    }

    public AbstractCreativeTab(String label, Supplier<ItemStack> icon) {
        this(GROUPS.length, label, icon);
    }

    public AbstractCreativeTab(int index, String label, Supplier<ItemStack> icon) {
        super(index, label);
        this.icon = icon;
        initStack();
    }

    @OnlyIn(Dist.CLIENT)
    private ItemStack clientStack;
    private final Supplier<ItemStack> icon;

    private void initStack() {
        if (FMLHelper.getDist() == Dist.CLIENT) {
            clientStack = icon.get();
        }
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack createIcon() {
        return this.clientStack;
    }

}
