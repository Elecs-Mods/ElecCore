package elec332.core.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 23-2-2015.
 */
public abstract class AbstractPacket implements IMessage, IMessageHandler<AbstractPacket, IMessage> {

    public AbstractPacket(){
    }

    public AbstractPacket(NBTTagCompound b){
        this.networkPackageObject = b;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkPackageObject = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.networkPackageObject);
    }

    public NBTTagCompound networkPackageObject;

    @Override
    public abstract IMessage onMessage(AbstractPacket message, MessageContext ctx);
}
