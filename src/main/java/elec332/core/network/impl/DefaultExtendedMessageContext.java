package elec332.core.network.impl;

import com.google.common.base.Preconditions;
import elec332.core.ElecCore;
import elec332.core.api.network.IExtendedMessageContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDispatcher;

/**
 * Created by Elec332 on 3-12-2016.
 */
class DefaultExtendedMessageContext implements IExtendedMessageContext {

    DefaultExtendedMessageContext(NetworkEvent.Context messageContext) {
        this.messageContext = messageContext;
    }

    private final NetworkEvent.Context messageContext;

    @Override
    public LogicalSide getReceptionSide() {
        return messageContext.getDirection().getReceptionSide();
    }

    @Override
    public PlayerEntity getSender() {
        return getReceptionSide() == LogicalSide.CLIENT ? ElecCore.proxy.getClientPlayer() : messageContext.getSender();
    }

    @Override
    public World getWorld() {
        return getReceptionSide() == LogicalSide.CLIENT ? ElecCore.proxy.getClientWorld() : Preconditions.checkNotNull(messageContext.getSender()).getEntityWorld();
    }

    @Override
    public PacketDispatcher getNetworkManager() {
        return messageContext.getPacketDispatcher();
    }

}
