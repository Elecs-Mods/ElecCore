package elec332.core.network.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.api.network.INetworkManager;
import elec332.core.api.network.IPacketRegistry;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.util.FMLHelper;
import elec332.core.util.FieldPointer;
import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 23-10-2016.
 */
@StaticLoad
enum NetworkManager implements INetworkManager<DefaultNetworkHandler> {

    INSTANCE;

    NetworkManager() {
        this.networkHandlers = Maps.newHashMap();
        (new FieldPointer<ElecByteBuf, Function<ByteBuf, ElecByteBuf>>(ElecByteBuf.class, "factory")).set(null, ElecByteBufImpl::new);
    }

    private Map<ModContainer, DefaultNetworkHandler> networkHandlers;

    @Override
    public DefaultNetworkHandler getNetworkHandler(Object mod) {
        ModContainer mc = FMLHelper.getModContainer(mod);
        if (mc == null) {
            throw new IllegalArgumentException("No ModContainer found for: " + mod);
        }
        DefaultNetworkHandler ret = networkHandlers.get(mc);
        if (ret == null) {
            try {
                ret = new DefaultNetworkHandler(new ResourceLocation(mc.getModId(), "networkhandler"));
            } catch (RuntimeException e) { //Name already exists...
                ret = new DefaultNetworkHandler(new ResourceLocation(mc.getModId(), "networkmanager"));
            }
            networkHandlers.put(mc, Preconditions.checkNotNull(ret));
        }
        return ret;
    }

    @Override
    public DefaultNetworkHandler createNetworkHandler(Object mod, ResourceLocation channelName, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        ModContainer mc = FMLHelper.getModContainer(mod);
        if (mc == null) {
            throw new IllegalArgumentException("No ModContainer found for: " + mod);
        }
        DefaultNetworkHandler ret = networkHandlers.get(mc);
        if (ret != null) {
            throw new IllegalArgumentException("Mod " + mc.getModId() + " already has a NetworkHandler!");
        }
        networkHandlers.put(mc, ret = new DefaultNetworkHandler(channelName, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
        return ret;
    }

    @Override
    public DefaultNetworkHandler createNetworkHandler(Object mod, SimpleChannel simpleNetworkWrapper) {
        ModContainer mc = FMLHelper.getModContainer(mod);
        if (mc == null) {
            throw new IllegalArgumentException("No ModContainer found for: " + mod);
        }
        DefaultNetworkHandler ret = networkHandlers.get(mc);
        if (ret != null) {
            throw new IllegalArgumentException("Mod " + mc.getModId() + " already has a NetworkHandler!");
        }
        networkHandlers.put(mc, ret = new DefaultNetworkHandler(simpleNetworkWrapper));
        return ret;
    }

    @Override
    public ISimpleNetworkPacketManager getSimpleNetworkManager(Object mod) {
        return getNetworkHandler(mod);
    }

    @Override
    public ISimpleNetworkPacketManager getAdditionalSimpleNetworkManager(Object mod, ResourceLocation name) {
        return getNetworkHandler(mod).getSimpleNetworkManager(name);
    }

    @Override
    public IPacketRegistry newPacketRegistry() {
        return new DefaultPacketRegistry();
    }

    @Override
    public IExtendedMessageContext wrapMessageContext(NetworkEvent.Context messageContext) {
        return new DefaultExtendedMessageContext(messageContext);
    }

    @APIHandlerInject
    public void injectNetworkManager(IAPIHandler apiHandler) {
        apiHandler.inject(INSTANCE, INetworkManager.class);
    }

}
