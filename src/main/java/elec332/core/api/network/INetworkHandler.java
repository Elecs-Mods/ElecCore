package elec332.core.api.network;

import elec332.core.api.network.object.INetworkObjectManager;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface INetworkHandler extends IPacketDispatcher, IPacketRegistry, INetworkObjectManager, ISimpleNetworkPacketManager {

}
