package elec332.core.api.network.object;

import elec332.core.api.network.ElecByteBuf;

/**
 * Created by Elec332 on 24-10-2016.
 */
public interface INetworkObjectReceiver {

    public void onPacket(int id, ElecByteBuf data);

    default public void setNetworkObjectHandler(INetworkObjectHandler handler) {
    }

}
