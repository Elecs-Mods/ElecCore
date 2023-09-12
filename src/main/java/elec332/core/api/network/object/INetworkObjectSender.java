package elec332.core.api.network.object;

import elec332.core.api.network.ElecByteBuf;

/**
 * Created by Elec332 on 24-10-2016.
 */
public interface INetworkObjectSender {

    default public void writePacket(int id, ElecByteBuf data) {
    }

    public void setNetworkObjectHandler(INetworkObjectHandler handler);

}
