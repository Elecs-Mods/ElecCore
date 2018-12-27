package elec332.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 11-10-2015.
 *
 * Null/fake container
 */
public final class ContainerNull extends Container {

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        return false;
    }

}
