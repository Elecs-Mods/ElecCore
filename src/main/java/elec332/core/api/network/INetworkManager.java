package elec332.core.api.network;

import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface INetworkManager<T extends INetworkHandler> {

    public T getNetworkHandler(Object mod);

    public T createNetworkHandler(Object mod, SimpleChannel simpleNetworkWrapper);

    public T createNetworkHandler(Object mod, ResourceLocation channelName, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions);

    public ISimpleNetworkPacketManager getSimpleNetworkManager(Object mod);

    public ISimpleNetworkPacketManager getAdditionalSimpleNetworkManager(Object mod, ResourceLocation name);

    public IPacketRegistry newPacketRegistry();

    public IExtendedMessageContext wrapMessageContext(NetworkEvent.Context messageContext);

}
