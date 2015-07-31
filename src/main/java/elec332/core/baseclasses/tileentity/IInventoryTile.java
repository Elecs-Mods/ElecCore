package elec332.core.baseclasses.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * Created by Elec332 on 1-5-2015.
 */
public interface IInventoryTile {

    public Container getGuiServer(EntityPlayer player);


    public Object getGuiClient(EntityPlayer player);
}
