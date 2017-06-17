package elec332.core.mcabstractionlayer.object;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 30-1-2017.
 */
public interface IAbstractedSlot {

    @Nonnull
    public ItemStack onTakenFromSlotC(EntityPlayer player, ItemStack stack);

    public void onSwapCraftC(int count);

}
