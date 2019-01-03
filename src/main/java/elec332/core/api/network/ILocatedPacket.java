package elec332.core.api.network;

import net.minecraft.world.World;

/**
 * Created by Elec332 on 24-11-2017.
 */
public interface ILocatedPacket {

    public IPacketDispatcher.TargetPoint getTargetPoint(World world, double range);

}
