package elec332.core.network;

import elec332.core.main.ElecCore;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
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
    public IMessage onMessage(final AbstractPacket message, final MessageContext ctx) {
        ElecCore.tickHandler.registerCall(new Runnable() {
            @Override
            public void run() {
                Object o = onMessageThreadSafe(message, ctx);
                if (o != null)
                    throw new RuntimeException();
            }
        }, ctx.side);
        return null;
    }

    public abstract IMessage onMessageThreadSafe(AbstractPacket message, MessageContext ctx);

}
