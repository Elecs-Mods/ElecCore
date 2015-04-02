package elec332.core.network;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * Created by Elec332 on 23-2-2015.
 */
public class NetworkHandler {
    public NetworkHandler(String channelName){
        this.channelName = channelName.toLowerCase();
        this.networkWrapper = new SimpleNetworkWrapper(this.channelName);
    }

    String channelName;
    int i = 0;

    SimpleNetworkWrapper networkWrapper;

    public void registerServerPacket(Class<? extends AbstractPacket> packetClass){
        register(packetClass, Side.SERVER);
    }

    public void registerClientPacket(Class<? extends AbstractPacket> packetClass){
        register(packetClass, Side.CLIENT);
    }

    public SimpleNetworkWrapper getNetworkWrapper() {
        return networkWrapper;
    }

    public String getChannelName() {
        return channelName;
    }

    @SuppressWarnings("unchecked")
    private void register(Class packetClass, Side side){
        networkWrapper.registerMessage(packetClass, packetClass, i, side);
        ++i;
    }
}
