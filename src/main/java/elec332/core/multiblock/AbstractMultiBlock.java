package elec332.core.multiblock;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Elec332 on 28-7-2015.
 */
public abstract class AbstractMultiBlock extends IMultiBlock {

    public abstract boolean onAnyBlockActivated(EntityPlayer player);

}
