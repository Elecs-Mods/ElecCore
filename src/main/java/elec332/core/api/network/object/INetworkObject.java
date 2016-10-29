package elec332.core.api.network.object;

/**
 * Created by Elec332 on 23-10-2016.
 */
public interface INetworkObject extends INetworkObjectSender, INetworkObjectReceiver {

    @Override
    default public void setNetworkObjectHandler(INetworkObjectHandler handler){
    }

}
