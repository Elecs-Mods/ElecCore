package elec332.core.api.network;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * Created by Elec332 on 24-11-2017.
 */
public interface ILocatedPacket {

    public NetworkRegistry.TargetPoint getTargetPoint(World world, double range);

}
