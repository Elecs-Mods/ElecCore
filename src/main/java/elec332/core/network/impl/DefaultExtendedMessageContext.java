package elec332.core.network.impl;

import elec332.core.ElecCore;
import elec332.core.api.network.IExtendedMessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Elec332 on 3-12-2016.
 */
class DefaultExtendedMessageContext implements IExtendedMessageContext {

    DefaultExtendedMessageContext(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    private final MessageContext messageContext;

    @Override
    public Side getSide() {
        return messageContext.side;
    }

    @Override
    public EntityPlayer getSender() {
        return getSide().isClient() ? ElecCore.proxy.getClientPlayer() : getServerHandler().player;
    }

    @Override
    public World getWorld() {
        return getSide().isClient() ? ElecCore.proxy.getClientWorld() : getServerHandler().player.getEntityWorld();
    }

    @Override
    public INetHandler getNetHandler() {
        return messageContext.netHandler;
    }

    @Override
    public void sendPacket(Packet<?> packetIn) {
        if (getSide().isClient()) {
            getClientHandler().sendPacket(packetIn);
        } else {
            getServerHandler().sendPacket(packetIn);
        }
    }

    @Override
    public NetworkManager getNetworkManager() {
        return getSide().isClient() ? getClientHandler().getNetworkManager() : getServerHandler().getNetworkManager();
    }

}
