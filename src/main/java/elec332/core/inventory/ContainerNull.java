package elec332.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Created by Elec332 on 11-10-2015.
 */
public final class ContainerNull extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return false;
    }

}
