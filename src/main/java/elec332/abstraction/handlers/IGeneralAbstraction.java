package elec332.abstraction.handlers;

import elec332.core.util.AbstractCreativeTab;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 28-1-2017.
 */
public interface IGeneralAbstraction {

    @Nonnull
    public AbstractCreativeTab create(int index, String label, Supplier<ItemStack> icon);

}
