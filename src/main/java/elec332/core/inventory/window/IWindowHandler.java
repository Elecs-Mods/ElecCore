package elec332.core.inventory.window;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 29-11-2016.
 */
public interface IWindowHandler {

    public Window createWindow(byte ID, EntityPlayer player, World world, int x, int y, int z);

}
