package elec332.core.inventory.window;

import elec332.core.api.network.ElecByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 29-11-2016.
 */
public interface IWindowHandler {

    public Window createWindow(PlayerEntity player, World world, ElecByteBuf data);

}
