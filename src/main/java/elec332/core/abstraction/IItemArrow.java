package elec332.core.abstraction;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 21-12-2016.
 */
public interface IItemArrow extends IItem {

    @Nonnull
    default public EntityArrow createArrow(@Nonnull World world, @Nonnull ItemStack stack, EntityLivingBase shooter) {
        return getFallback().createArrow(world, stack, shooter);
    }

    default public boolean isInfinite(ItemStack arrow, ItemStack bow, EntityPlayer shooter) {
        return getFallback().isInfinite(arrow, bow, shooter);
    }

    default public IItemArrow getFallback(){
        return DefaultInstances.DEFAULT_ARROW;
    }

}
