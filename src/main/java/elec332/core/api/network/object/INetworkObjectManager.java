package elec332.core.api.network.object;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface INetworkObjectManager {

    public <N extends INetworkObjectReceiver> INetworkObjectHandler<?> registerNetworkObject(N networkObject);

    public <R extends INetworkObjectReceiver, S extends INetworkObjectSender> INetworkObjectHandler<S> registerNetworkObject(@Nullable R networkObjectR, @Nullable S networkObjectS);

    public <N extends INetworkObject> INetworkObjectHandler<N> registerSpecialNetworkObject(N networkObject);

}
