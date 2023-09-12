package elec332.core.api.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.PacketDispatcher;

/**
 * Created by Elec332 on 3-12-2016.
 */
public interface IExtendedMessageContext {

    public LogicalSide getReceptionSide();

    public PlayerEntity getSender();

    public World getWorld();

    public PacketDispatcher getNetworkManager();

}
