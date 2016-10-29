package elec332.core.api.network;

import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface INetworkManager<T extends INetworkHandler> {

    public T getNetworkHandler(Object mod);

    public T createNetworkHandler(Object mod, SimpleNetworkWrapper simpleNetworkWrapper);

    public ISimpleNetworkPacketManager getSimpleNetworkManager(Object mod);

    public ISimpleNetworkPacketManager getAdditionalSimpleNetworkManager(Object mod, String name);

    public IPacketRegistry newPacketRegistry();

}
