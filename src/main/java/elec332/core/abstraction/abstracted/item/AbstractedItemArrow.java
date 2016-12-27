package elec332.core.abstraction.abstracted.item;

import elec332.core.abstraction.IItemArrow;
import elec332.core.abstraction.abstracted.CopyMarker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 24-12-2016.
 */
abstract class AbstractedItemArrow extends ItemArrow implements IAbstractedItem<IItemArrow> {

    @Override @CopyMarker
    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        return getLinkedItem_INTERNAL_ELEC().createArrow(worldIn, stack, shooter);
    }

    @Override @CopyMarker
    public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player) {
        return getLinkedItem_INTERNAL_ELEC().isInfinite(stack, bow, player);
    }

}
