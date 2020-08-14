package elec332.core.api.network.object;

import elec332.core.api.network.ElecByteBuf;

/**
 * Created by Elec332 on 24-10-2016.
 */
public interface INetworkObjectSender<N extends INetworkObjectSender<N>> {

    default void writePacket(int id, ElecByteBuf data) {
    }

    void setNetworkObjectHandler(INetworkObjectHandler<N> handler);

}
