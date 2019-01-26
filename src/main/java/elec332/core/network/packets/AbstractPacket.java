package elec332.core.network.packets;

import elec332.core.ElecCore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 23-2-2015.
 */
public abstract class AbstractPacket extends AbstractMessage implements IMessageHandler<AbstractPacket, IMessage> {

    public AbstractPacket() {
        super((NBTTagCompound) null);
    }

    public AbstractPacket(Supplier<NBTTagCompound> dataSupplier) {
        super(dataSupplier);
    }

    public AbstractPacket(NBTTagCompound b) {
        super(b);
    }

    @Override
    @Deprecated //Warning, not thread safe!
    public IMessage onMessage(final AbstractPacket message, final MessageContext ctx) {
        ElecCore.tickHandler.registerCall(new Runnable() {
            @Override
            public void run() {
                Object o = onMessageThreadSafe(message.networkPackageObject, ctx);
                if (o != null) {
                    throw new RuntimeException();
                }
            }
        }, ctx.side);
        return null;
    }

    public abstract IMessage onMessageThreadSafe(NBTTagCompound message, MessageContext ctx);

}
