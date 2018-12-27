package elec332.core.api.network;

/**
 * Created by Elec332 on 23-10-2016.
 *
 * A container that can register packets to an {@link IPacketRegistry}
 */
public interface IPacketRegistryContainer {

    /**
     * Gets called when this container is supposed to register
     * its packets to the provided {@link IPacketRegistry}
     *
     * @param packetRegistry The packet registry to register the packets to
     */
    public void registerPacketsTo(IPacketRegistry packetRegistry);

}
