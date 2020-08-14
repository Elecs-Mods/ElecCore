package elec332.core.api.network.object;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface INetworkObject<N extends INetworkObjectSender<N>> extends INetworkObjectSender<N>, INetworkObjectReceiver<N> {

    @Override
    default void setNetworkObjectHandler(INetworkObjectHandler<N> handler) {
    }

}
