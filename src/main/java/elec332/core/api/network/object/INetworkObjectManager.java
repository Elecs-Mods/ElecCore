package elec332.core.api.network.object;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface INetworkObjectManager {

    <N extends INetworkObjectReceiver<S>, S extends INetworkObjectSender<S>> INetworkObjectHandler<?> registerNetworkObject(N networkObject);

    <R extends INetworkObjectReceiver<S>, S extends INetworkObjectSender<S>> INetworkObjectHandler<S> registerNetworkObject(@Nullable R networkObjectR, @Nullable S networkObjectS);

    <N extends INetworkObject<N>> INetworkObjectHandler<N> registerSpecialNetworkObject(N networkObject);

}
