package elec332.core.network.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.api.network.INetworkManager;
import elec332.core.api.network.IPacketRegistry;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.util.FMLUtil;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Elec332 on 23-10-2016.
 */
@StaticLoad
enum NetworkManager implements INetworkManager<DefaultNetworkHandler> {

    INSTANCE;

    NetworkManager() {
        this.networkHandlers = Maps.newHashMap();
    }

    private Map<ModContainer, DefaultNetworkHandler> networkHandlers;

    @Override
    public DefaultNetworkHandler getNetworkHandler(Object mod) {
        ModContainer mc = FMLUtil.getModContainer(mod);
        if (mc == null) {
            throw new IllegalArgumentException("No ModContainer found for: " + mod);
        }
        DefaultNetworkHandler ret = networkHandlers.get(mc);
        if (ret == null) {
            String name = mc.getModId();
            try {
                ret = new DefaultNetworkHandler(name);
            } catch (RuntimeException e) { //Name already exists, try to import...
                String name2 = UUID.randomUUID().toString();
                SimpleNetworkWrapper wrapperFor = new SimpleNetworkWrapper(name2);
                EnumMap<Side, FMLEmbeddedChannel> channels = DefaultNetworkHandler.getNetworkChannels(wrapperFor);
                EnumMap<Side, Map<String, FMLEmbeddedChannel>> handler = DefaultNetworkHandler.getRegistryChannels();
                for (Side side : Side.values()) {
                    channels.put(side, handler.get(side).get(name));
                    handler.get(side).remove(name2);
                }
                ret = new DefaultNetworkHandler(wrapperFor);
            }
            networkHandlers.put(mc, Preconditions.checkNotNull(ret));
        }
        return ret;
    }

    @Override
    public DefaultNetworkHandler createNetworkHandler(Object mod, SimpleNetworkWrapper simpleNetworkWrapper) {
        ModContainer mc = FMLUtil.getModContainer(mod);
        if (mc == null) {
            throw new IllegalArgumentException("No ModContainer found for: " + mod);
        }
        DefaultNetworkHandler ret = networkHandlers.get(mc);
        if (ret != null) {
            throw new IllegalArgumentException("Mod " + mc.getName() + " already has a NetworkHandler!");
        }
        networkHandlers.put(mc, ret = new DefaultNetworkHandler(simpleNetworkWrapper));
        return ret;
    }

    @Override
    public ISimpleNetworkPacketManager getSimpleNetworkManager(Object mod) {
        return getNetworkHandler(mod);
    }

    @Override
    public ISimpleNetworkPacketManager getAdditionalSimpleNetworkManager(Object mod, String name) {
        return getNetworkHandler(mod).getSimpleNetworkManager(name);
    }

    @Override
    public IPacketRegistry newPacketRegistry() {
        return new DefaultPacketRegistry();
    }

    @Override
    public IExtendedMessageContext wrapMessageContext(MessageContext messageContext) {
        return null;
    }

    @APIHandlerInject
    public void injectNetworkManager(IAPIHandler apiHandler) {
        apiHandler.inject(INSTANCE, INetworkManager.class);
    }

}
